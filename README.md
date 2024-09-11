# Toggle Toggle Sprint

[![Requires Fabric API](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/requires/fabric-api_vector.svg)](https://modrinth.com/mod/fabric-api)
[![Available on Modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/modrinth_vector.svg)](https://modrinth.com/mod/toggle-toggle-sprint)

This is a Fabric mod adding Toggle Sprint and Toggle Sneak keybinds in modern versions of Minecraft, making use of the
existing Toggle options in the Controls menu, ensuring it also remains as anti-cheat friendly as possible.

This also includes a fix for preventing the game from resetting your toggle sprint upon death (only applicable for 1.20.1+).

If both [Mod Menu] and [YACL] are installed, this mod also contains a few configuration options that can be modified in-game.

## How is this any different from other mods?

I originally designed this to replace [Toggle Sneak & Sprint], which instead of doing the sensible thing of making the
game think that you're holding the Sprint key, it instead simply forces you to *always* sprint *(even when it'd normally
be impossible to do so!)*, making it trivially easy to be flagged (and subsequently banned) by a server's anti-cheat.

Instead, this mod simply re-uses the existing Toggle control logic, ensuring that it remains as anti-cheat friendly as
possible by simply deferring the actual sprinting logic to the base game.

[YACL]: https://modrinth.com/mod/yacl
[Mod Menu]: https://modrinth.com/mod/modmenu
[Toggle Sneak & Sprint]: https://modrinth.com/mod/toggle-sneak-sprint
