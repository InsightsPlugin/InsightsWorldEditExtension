plugins {
    id("net.minecrell.plugin-yml.bukkit") version VersionConstants.pluginYmlVersion
}

dependencies {
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:${VersionConstants.worldeditVersion}")
}

bukkit {
    main = "dev.frankheijden.insights.extensions.worldedit.InsightsWorldEditExtension"
    name = "InsightsWorldEditExtension"
    description = "An Insights extension for WorldEdit limits"
    apiVersion = "1.18"
    website = "https://github.com/InsightsPlugin/Insights"
    depend = listOf("Insights", "WorldEdit")
    softDepend = listOf("FastAsyncWorldEdit")
    authors = listOf("FrankHeijden")
    commands {
        register("insightswe") {
            aliases = listOf("insightsworldedit")
            permission = "insights.worldedit.reload"
            usage = "/<command> reload"
        }
    }
}
