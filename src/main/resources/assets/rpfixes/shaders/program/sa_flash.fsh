#version 150

// ---------------------------------------------------------------------------
// Survivor's Arsenal - flashlight
//
// Spotlight projetada em world-space, com tres lobulos concentricos:
//
//   core  - ponto forte, e a mancha que voce mira
//   spill - queda do refletor em volta do ponto
//   wash  - banho largo e fraco, deixa o ambiente legivel
//
// Pipeline:
//   1. le o depth buffer da cena
//   2. reconstroi a posicao view-space do pixel via InvProjMat
//   3. rotaciona pra world-space relativo a camera via InvViewMat
//   4. acumula a contribuicao de ate 4 fachos
//
// MULTIPLOS FACHOS: uniform do Minecraft nao aceita array livre, entao os
// fachos vem empacotados em duas mat4, uma coluna por facho:
//   BeamOrigin[i] = vec4(posicao relativa a camera, alcance)
//   BeamAxis[i]   = vec4(direcao normalizada,       intensidade)
// BeamCount diz quantas colunas valem. Isso e o que permite ver a lanterna de
// outros jogadores em multiplayer.
//
// Nao ha shaft volumetrico: o ar fica escuro, so superficie recebe luz.
// ---------------------------------------------------------------------------

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;

uniform mat4 ProjMat;      // usado pelo blit.vsh, nao mexer
uniform vec2 OutSize;      // idem

uniform mat4 InvProjMat;   // inversa da projecao do level
uniform mat4 InvViewMat;   // view -> world (rotacao pura, relativa a camera)

uniform mat4 BeamOrigin;   // coluna i = vec4(posicao, alcance)
uniform mat4 BeamAxis;     // coluna i = vec4(direcao, intensidade)
uniform float BeamCount;   // quantos fachos valem, 0..4

uniform vec2 CoreCone;     // x = cos(inner), y = cos(outer)
uniform vec2 SpillCone;
uniform vec2 WashCone;
uniform vec2 LobeWeights;  // x = peso do spill, y = peso do wash
uniform vec3 LightColor;
uniform float ShaderPackMode;
// RP Fixes: 1 when FL Render lights the scene for real (its flashlight cone lights blocks and entities as they are
// drawn). Then this pass only underlines the spot on an already lit scene: much lower gain, no lift.
uniform float FlCone;

in vec2 texCoord;
out vec4 fragColor;

// --- resposta da luz na superficie ------------------------------------------
// GAIN  - ganho multiplicativo. E o que realmente ilumina: multiplica a cor que
//         o bloco ja tem, entao textura, contraste e matiz sobrevivem.
// LIFT  - soma aditiva pequena, so pra pixel quase preto nao continuar preto
//         dentro do facho. Subir muito isso traz a camada branca de volta.
// TINT  - quanto o lift e tingido pela cor do proprio bloco (1.0 = totalmente).
// NDL_FLOOR - quanto uma superficie de raspao ainda recebe. Subir achata o
//             relevo, descer realca volume mas realca tambem o degrau do depth.
// BEAM_KNEE - onde comeca a compressao suave. Sem ela o canal mais forte bate
//             no teto 1.0 antes dos outros, a razao entre eles se perde e a cor
//             anda pra branco. E ESSA a causa do aspecto de filtro branco.
// BEAM_SAT  - devolve o croma que a cena escura ja tinha achatado. E o que da
//             a leitura de "night vision": a cor do bloco aparece, nao um veu.
const float BEAM_GAIN = 6.40;
const float BEAM_LIFT = 0.09;
const float BEAM_TINT = 1.00;
const float NDL_FLOOR = 0.42;
const float BEAM_KNEE = 0.90;
const float BEAM_SAT  = 1.20;

// RP Fixes: the same with FL Render's real flashlight light underneath (FlCone = 1).
const float CONE_GAIN = 0.55;
const float CONE_LIFT = 0.0;
const float CONE_KNEE = 0.85;
const float CONE_SAT  = 1.0;

// CORE_HOT   - brilho extra so no miolo do nucleo, sem mexer nas bordas.
// EDGE_NOISE - irregularidade da borda. 0.0 = circulo geometrico perfeito.
//              Acima de ~0.008 comeca a deformar o circulo em vez de sujar.
const float CORE_HOT   = 0.62;
const float EDGE_NOISE = 0.0035;

const int MAX_BEAMS = 4;

const vec3 LUMA = vec3(0.2126, 0.7152, 0.0722);

float hash21(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float valueNoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = hash21(i);
    float b = hash21(i + vec2(1.0, 0.0));
    float c = hash21(i + vec2(0.0, 1.0));
    float d = hash21(i + vec2(1.0, 1.0));
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

// Desprojeta o pixel: depth nao-linear -> posicao view-space.
vec3 viewPosFromDepth(vec2 uv, float rawDepth) {
    vec4 ndc = vec4(uv * 2.0 - 1.0, rawDepth * 2.0 - 1.0, 1.0);
    vec4 view = InvProjMat * ndc;
    float w = abs(view.w) < 1e-6 ? 1e-6 : view.w;
    return view.xyz / w;
}

// Mesma reconstrucao, mas em um uv arbitrario. Usado pra amostrar os vizinhos
// na hora de montar a normal.
vec3 worldPosAt(vec2 uv) {
    float d = texture(DiffuseDepthSampler, uv).r;
    return (InvViewMat * vec4(viewPosFromDepth(uv, d), 0.0)).xyz;
}

/**
 * Contribuicao de UM facho no pixel.
 *
 * worldPos e normal ja vem prontos e sao compartilhados por todos os fachos:
 * a geometria do pixel nao muda de uma lanterna pra outra, so a luz muda.
 */
float beamContribution(vec3 worldPos, vec3 normal, float normalConfidence,
                       vec3 lampPos, float range, vec3 lampDir, float intensity) {
    vec3 toFrag = worldPos - lampPos;
    float dist = length(toFrag);
    vec3 L = toFrag / max(dist, 1e-5);
    float cosA = dot(L, lampDir);

    // Descarta cedo o que esta atras da lanterna: sem isso o lobulo largo
    // vaza pelas costas quando o cone abre muito.
    if (cosA < WashCone.y - 0.05) {
        return 0.0;
    }

    // Base ortonormal do facho. O ruido e amostrado em COORDENADA DE DISCO,
    // nao no angulo: com atan() o valor da a volta em +-pi e o noise nao fecha
    // ali, o que criava um entalhe fixo em um lado do circulo.
    vec3 upRef = abs(lampDir.y) > 0.94 ? vec3(1.0, 0.0, 0.0) : vec3(0.0, 1.0, 0.0);
    vec3 tx = normalize(cross(upRef, lampDir));
    vec3 ty = cross(lampDir, tx);
    vec2 disc = vec2(dot(L, tx), dot(L, ty));

    float edgeNoise = (valueNoise(disc * 7.0) - 0.5) * EDGE_NOISE;
    edgeNoise += (valueNoise(disc * 19.0 + 4.0) - 0.5) * EDGE_NOISE * 0.45;
    // Shaderpacks ja usam dithering/blue-noise. Somar outro ruido na borda
    // deixa o facho granulado, entao o desligamos apenas nesse caminho.
    edgeNoise *= 1.0 - clamp(ShaderPackMode, 0.0, 1.0);

    // --- tres lobulos ------------------------------------------------------
    float core = smoothstep(CoreCone.y, CoreCone.x, cosA + edgeNoise);
    core = core * core * (3.0 - 2.0 * core);   // ombro mais marcado no nucleo

    float spill = smoothstep(SpillCone.y, SpillCone.x, cosA);
    spill *= 0.45 + 0.55 * spill;

    float wash = smoothstep(WashCone.y, WashCone.x, cosA + edgeNoise * 0.6);
    wash *= 0.55 + 0.45 * wash;

    // Miolo do nucleo: brilho extra concentrado, sem tocar nas bordas.
    float hot = smoothstep(CoreCone.x, mix(CoreCone.x, 1.0, 0.65), cosA);

    // Segundo circulo central exclusivo do caminho Iris/Oculus.
    // Ele e mais fechado que o core principal e apenas um pouco mais forte,
    // dando leitura de hotspot sem voltar ao estouro branco do patch anterior.
    float shaderCenter = smoothstep(
        mix(CoreCone.x, 1.0, 0.18),
        mix(CoreCone.x, 1.0, 0.58),
        cosA
    );
    shaderCenter = shaderCenter * shaderCenter * (3.0 - 2.0 * shaderCenter);

    float shaderMode = clamp(ShaderPackMode, 0.0, 1.0);
    float shape = min(core + hot * CORE_HOT
                      + shaderCenter * 0.18 * shaderMode
                      + spill * LobeWeights.x + wash * LobeWeights.y, 1.75);

    // --- atenuacao por distancia ------------------------------------------
    float safeRange = max(range, 0.001);
    float atten = 1.0 / (1.0 + dist * 0.15 + dist * dist * 0.014);
    atten *= 1.0 - smoothstep(safeRange * 0.62, safeRange, dist);
    atten *= 1.55;

    // Encostado na parede a lampada estouraria; segura os primeiros centimetros.
    atten *= smoothstep(0.05, 0.50, dist);

    // --- incidencia --------------------------------------------------------
    // Piso alto de proposito: a quantizacao do depth buffer (24 bits) ja deixa
    // a normal levemente degrauzada em superficie plana e distante. Quanto mais
    // o N.L manda no resultado, mais esse degrau aparece.
    float ndl = max(dot(normal, -L), 0.0);
    ndl = mix(0.80, mix(NDL_FLOOR, 1.0, ndl), normalConfidence);

    return shape * atten * ndl * intensity;
}

void main() {
    vec4 source = texture(DiffuseSampler, texCoord);
    vec3 color = source.rgb;

    float rawDepth = texture(DiffuseDepthSampler, texCoord).r;

    // Ceu / far plane nao recebe luz. Mascara em vez de "return" antecipado:
    // derivada em fluxo de controle nao-uniforme e indefinida.
    float surfaceMask = 1.0 - smoothstep(0.99985, 1.0, rawDepth);

    vec3 viewPos = viewPosFromDepth(texCoord, rawDepth);
    vec3 worldPos = (InvViewMat * vec4(viewPos, 0.0)).xyz;   // relativo a camera

    // --- normal por pixel --------------------------------------------------
    // No vanilla reconstruimos a normal usando quatro amostras de depth.
    // Sob Iris/Oculus o depth final pode conter jitter temporal e formatos do
    // shaderpack; amplificar essa normal gera o "granulado" que aparece quando
    // a lanterna liga. Nesse caminho usamos incidencia neutra e estavel.
    vec3 viewRay = normalize(worldPos + vec3(1e-5));
    vec3 normal = -viewRay;
    float normalConfidence = 0.0;

    if (ShaderPackMode < 0.5) {
        vec2 texel = 1.0 / max(OutSize, vec2(1.0));

        vec3 pL = worldPosAt(texCoord - vec2(texel.x, 0.0));
        vec3 pR = worldPosAt(texCoord + vec2(texel.x, 0.0));
        vec3 pD = worldPosAt(texCoord - vec2(0.0, texel.y));
        vec3 pU = worldPosAt(texCoord + vec2(0.0, texel.y));

        float centerDist = length(worldPos);
        float gapL = abs(length(pL) - centerDist);
        float gapR = abs(length(pR) - centerDist);
        float gapD = abs(length(pD) - centerDist);
        float gapU = abs(length(pU) - centerDist);

        vec3 dx = gapR < gapL ? (pR - worldPos) : (worldPos - pL);
        vec3 dy = gapU < gapD ? (pU - worldPos) : (worldPos - pD);

        normal = normalize(cross(dx, dy));
        if (dot(normal, -viewRay) < 0.0) {
            normal = -normal;
        }

        float disagree = (min(gapL, gapR) + min(gapD, gapU)) / max(centerDist, 1.0);
        normalConfidence = 1.0 - smoothstep(0.012, 0.085, disagree);
    }

    // --- acumula os fachos -------------------------------------------------
    float light = 0.0;
    for (int i = 0; i < MAX_BEAMS; i++) {
        if (float(i) >= BeamCount) {
            break;
        }
        vec4 origin = BeamOrigin[i];
        vec4 axis = BeamAxis[i];
        light += beamContribution(worldPos, normal, normalConfidence,
                                  origin.xyz, origin.w, axis.xyz, axis.w);
    }

    // --- composicao --------------------------------------------------------
    float shaderMode = clamp(ShaderPackMode, 0.0, 1.0);
    float luminance = dot(color, LUMA);
    float darkness = 1.0 - smoothstep(0.10, 0.92, luminance);

    light *= surfaceMask;
    light *= mix(0.84, 1.05, darkness);   // nao lava o que ja esta claro

    // Protege pixels que ja vieram brilhantes do shaderpack: emissive da
    // flashlight, icones fullbright e bloom ja resolvido nao recebem outro boost.
    float brightProtection = 1.0 - smoothstep(0.52, 0.90, luminance);
    light *= mix(1.0, brightProtection, shaderMode);
    light = clamp(light, 0.0, mix(1.6, 0.98, shaderMode));

    // Denoise bilateral muito leve somente dentro do facho e somente com
    // shaderpack. Reduz dithering/grain sem borrar bordas de profundidade.
    if (shaderMode > 0.5 && light > 0.001) {
        vec2 denoiseTexel = 1.0 / max(OutSize, vec2(1.0));
        vec3 filtered = color;
        float totalWeight = 1.0;

        vec2 offsets[4] = vec2[4](
            vec2(denoiseTexel.x, 0.0),
            vec2(-denoiseTexel.x, 0.0),
            vec2(0.0, denoiseTexel.y),
            vec2(0.0, -denoiseTexel.y)
        );

        for (int sampleIndex = 0; sampleIndex < 4; sampleIndex++) {
            vec2 sampleUv = clamp(texCoord + offsets[sampleIndex], vec2(0.0), vec2(1.0));
            float sampleDepth = texture(DiffuseDepthSampler, sampleUv).r;
            float depthWeight = 1.0 - smoothstep(0.00025, 0.0030, abs(sampleDepth - rawDepth));
            filtered += texture(DiffuseSampler, sampleUv).rgb * depthWeight;
            totalWeight += depthWeight;
        }

        vec3 denoised = filtered / max(totalWeight, 1e-4);
        color = mix(color, denoised, clamp(light * 0.20, 0.0, 0.18));
        luminance = dot(color, LUMA);
    }

    float flCone = clamp(FlCone, 0.0, 1.0) * (1.0 - shaderMode);
    float beamGain = mix(mix(BEAM_GAIN, 3.75, shaderMode), CONE_GAIN, flCone);
    float beamLift = mix(mix(BEAM_LIFT, 0.035, shaderMode), CONE_LIFT, flCone);
    float beamKnee = mix(mix(BEAM_KNEE, 0.82, shaderMode), CONE_KNEE, flCone);
    float beamSat = mix(mix(BEAM_SAT, 1.06, shaderMode), CONE_SAT, flCone);

    // Matiz do proprio bloco, normalizado pelo canal mais forte.
    // Em pixel quase preto essa divisao amplifica ruido, entao a confianca cai
    // e o tint volta pra branco de forma suave.
    float peak = max(color.r, max(color.g, color.b));
    float tintConfidence = smoothstep(0.008, 0.090, peak);
    vec3 tint = color / max(peak, 1e-3);
    vec3 surfaceTint = mix(vec3(1.0), tint, BEAM_TINT * tintConfidence);

    // 1) GANHO MULTIPLICATIVO - o grosso da iluminacao.
    //    Multiplicar preserva a razao entre os canais, entao o bloco fica
    //    claro sendo ele mesmo em vez de virar cinza claro.
    vec3 lit = color * (vec3(1.0) + light * beamGain * LightColor);

    // 2) LIFT ADITIVO - so o minimo pra tirar o quase-preto do zero, ja que
    //    multiplicacao sozinha nunca levanta um pixel exatamente em 0.
    //    Totalmente tingido pelo bloco: e o termo que branqueia se exagerar.
    vec3 headroom = max(vec3(0.0), vec3(1.0) - lit);
    lit += headroom * surfaceTint * LightColor * light * beamLift;

    // 3) COMPRESSAO SUAVE no brilho, nao por canal.
    //    Escalamos os tres canais pelo MESMO fator, entao matiz e saturacao
    //    passam intactos e nada estoura em branco.
    float litLum = dot(lit, LUMA);
    float rolled = litLum / (1.0 + max(0.0, litLum - beamKnee));
    lit *= rolled / max(litLum, 1e-4);

    // 4) SATURACAO - so onde o facho realmente chega, pra area escura nao
    //    ganhar ruido colorido de graca.
    float outLum = dot(lit, LUMA);
    lit = mix(vec3(outLum), lit, mix(1.0, beamSat, min(light * 1.6, 1.0)));

    fragColor = vec4(clamp(lit, 0.0, 1.0), source.a);
}
