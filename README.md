# RP Fixes

Маленькие заплатки чужих модов сборки сервера (раньше жили в RP Core). Ничего не требует. Утечки чинятся на сервере,
эмоции с Fresh Animations — у игрока, так что ставить и туда, и туда (с 1.1.0). Версии на сервере и у игрока можно не
сверять: заходят и без него.

| Заплатка | Мод | Что делает | Настройка (`config/rpfixes-common.toml`) |
|---|---|---|---|
| утечка стрелков | Tacz Attribute Add (`taa`) | не держит в памяти вышедших игроков и убранных стрелков | `fixes.taa_shooter_leak` |
| утечка игроков | Sanity: Descent Into Madness (`sanitydim`) | убирает вышедших игроков из таблицы спавна | `fixes.sanity_player_leak` |
| эмоции и Fresh Animations | Entity Model Features + player-animation-lib (Emotecraft, RP Sports) | пока на игроке играет эмоция, EMF рисует его обычной моделью (`vanilla`) или только останавливает свои анимации (`pause`) | `fixes.emf_emote_mode` (`vanilla` / `pause` / `off`) |

Каждая заплатка срабатывает, только если её мод установлен. Не подошла к версии мода — в лог предупреждение, и всё.

Сборка: JDK 17, `gradlew.bat build`.
