# InsightsWorldEditExtension
An extension for [Insights](https://github.com/InsightsPlugin/Insights) to limit WorldEdit actions accordingly.
This extension has the following features:

* Support Insights limits (users won't be able to place their "limited blocks").
* Disabling of **all** tile placement using WorldEdit.
* Customize the behaviour when a blocked material has been encountered
  (either **don't change** the block in the world at all,
  or define a custom **replacement block** to replace blocked materials with)
* Define extra blocked materials with bypass permissions (whitelist/blacklist mode).

## Configuration
The configuration is relatively simple:
```yaml
settings:
  type: "REPLACEMENT"
  replacement-block: "BEDROCK"
  use-limits: true
  disable-tiles: true
  extra-blocked:
    whitelist: false
    materials:
      - "DIRT"

messages:
  summary:
    header: "&8&m---------------=&r&8[&b&l WorldEdit Limit&r &8]&m=---------------"
    format: "&b %entry%&8: &7&l%count%"
    unchanged: "&3 Prevented a total of &b&l%count% &3blocks from being placed."
    replaced: "&3 Replaced a total of &b&l%count% &3blocks with &b%replacement%&3."
    footer: "&8&m-------------------------------------------------"
```

## Commands
* `/insightswe reload` (permission: **insights.worldedit.reload**) - reloads the configuration.

## Permissions
* `insights.worldedit.bypass.tiles` - allows bypass of the tile block (**settings.use-tiles**)
* `insights.worldedit.bypass.<MATERIAL>` - allows bypass of any extra block defined in the config (**settings.extra-blocked.materials**)
* `insights.worldedit.reload` - allows use of **/insightswe reload**
