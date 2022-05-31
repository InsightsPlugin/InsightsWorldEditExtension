dependencies {
    compileOnly(project(":Plugin"))
    compileOnly(project(":WE"))
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core:${VersionConstants.faweVersion}")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit:${VersionConstants.faweVersion}") { isTransitive = false }
}
