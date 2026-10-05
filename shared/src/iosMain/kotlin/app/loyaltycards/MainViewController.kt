package app.loyaltycards

import androidx.compose.ui.window.ComposeUIViewController
import app.loyaltycards.data.IosDriverFactory
import app.loyaltycards.di.AppContainer
import platform.UIKit.UIViewController

private val container by lazy { AppContainer(IosDriverFactory()) }

@Suppress("FunctionName", "unused") // Called from Swift.
fun MainViewController(): UIViewController = ComposeUIViewController { App(container) }
