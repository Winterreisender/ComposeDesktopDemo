import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

object MaterialDesktopWindow {

    interface MaterialMenuScope :ColumnScope {
        fun collapse()
    }
    @Composable
    fun MaterialMenu(text :String ,dropdownMenuItems :@Composable MaterialMenuScope.()->Unit) {

        var menuExpanded by remember { mutableStateOf(false) }
        Column {
            Text(text, modifier = Modifier.padding(6.dp,0.dp).clickable { menuExpanded = true ; } ,maxLines = 1 )

            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }, focusable = true) {
                object: MaterialMenuScope, ColumnScope by this { // 委托 GREAT! C++ 可用using
                    override fun collapse() {
                        menuExpanded = false;
                    }
                }.dropdownMenuItems()
            }
        }
    }


    /*
    interface MaterialMenuItemScope :ApplicationScope,WindowScope {
        fun collapseDropdownMenu()
    }

    @Composable
    fun WindowScope.MaterialMenu(text :String, dropdownMenuItems :@Composable MaterialMenuItemScope.()->Unit) {
        var menuExpanded by remember { mutableStateOf(false) }

        Text(text,modifier = Modifier.padding(6.dp,0.dp).clickable { menuExpanded = true } )
        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded =  false }, focusable = true) {
            object :MaterialMenuItemScope {
                override fun collapseDropdownMenu() {
                    menuExpanded = false
                }
            }.dropdownMenuItems()
        }
    }

    @Composable
    fun MaterialMenuItemScope.MaterialMenuItem2(text :String, callback :()->Unit) {
        DropdownMenuItem(onClick = {
            collapseDropdownMenu()
            callback()
        }) {
            Text(text)
        }
    }
    */

    @Preview
    @Composable
    fun WindowScope.MaterialWindowTopBar(
        title :String,
        menuItems: @Composable () -> Unit = {},
        windowState: WindowState,
        scaffoldState :ScaffoldState,
        modifier :Modifier = Modifier.height(32.dp)
    ) 
    = TopAppBar(modifier = modifier) {
        val coroutineScope = rememberCoroutineScope()
        WindowDraggableArea {
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                scaffoldState.drawerState.open()
                                /*delay(1000L)
                                scaffoldState.drawerState.close()*/
                            }
                        },
                        content = { Icon(Icons.Default.Menu, null) }
                    )

                    menuItems()
                }

                Text(title)

                Row(horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { windowState.isMinimized = true },
                        content = { Icon(Icons.Default.ArrowDropDown, null) },
                        modifier = Modifier
                    )
                    IconButton(
                        onClick = { windowState.placement = if(windowState.placement == WindowPlacement.Maximized) WindowPlacement.Floating else WindowPlacement.Maximized },
                        content = { Icon(Icons.Default.Add, null) },
                        modifier = Modifier
                    )
                    IconButton(
                        onClick = { exitProcess(0) },
                        content = { Icon(Icons.Default.Close, null) },
                        modifier = Modifier
                    )
                }
            }
        }
    }

    @Composable
    fun ApplicationScope.MaterialDesktopWindow(
        title: String = "",
        windowState: WindowState = rememberWindowState(size = DpSize(1024.dp, 768.dp)),
        menuItems: @Composable () -> Unit = {},
        bottomBar: @Composable () -> Unit = {},
        drawerContent:  @Composable() (ColumnScope.() -> Unit)? = null,
        content :@Composable (PaddingValues)->Unit )
    {
        Window(onCloseRequest = ::exitApplication, title = title, state = windowState, undecorated = true) {
            MaterialTheme(colors = if (isSystemInDarkTheme()) darkColors(Teal700, Teal200, Purple200) else lightColors(Teal700, Teal200, Purple200)) {
                var scaffoldState = rememberScaffoldState()

                Scaffold(
                    scaffoldState = scaffoldState,
                    modifier = Modifier.border(BorderStroke(1.dp, color = Color.Unspecified)),
                    topBar = { MaterialWindowTopBar(title, menuItems ,windowState, scaffoldState) },
                    bottomBar = bottomBar,
                    drawerContent = drawerContent,
                    content = content
                )
            }
        }
    }
}