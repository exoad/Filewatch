package net.exoad.filewatch

import net.exoad.filewatch.engine.AutomationController
import net.exoad.filewatch.engine.Job

fun delegatedMain(args: Array<String>) {
    AutomationController.registerJob(Job("Downloads", "C:\\Users\\${System.getProperty("user.name")}\\Downloads\\"))
}