import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.*
import kotlin.system.exitProcess


/**
 * 一套Material菜单栏组件,包含MenuBar,Menu,MenuItem三个层次
 *
 * example:
* ```kt
* Scaffold(
*     topBar = {
*         MMenuBar("Jetpack Compose Demo", windowState) {
*             MMenu("文件") {
*                 MMenuItem("新建") {
*             }
*             MMenuItem("打开") {
*                 pagination = Pages.HOME
*             }
*             Divider()
*             MMenuItem("退出") {
*                exitApplication()
*             }
*         }
*     }
* )
```
 *
 * @author Winterreisender
 * @version 0.1.0
 */


object MTopMenuBar {
    @Composable
    fun WindowScope.MMenuBar(
        title: String,
        windowState: WindowState,
        onIconClicked: ()->Unit = {},
        modifier: Modifier = Modifier.height(32.dp),
        menus: @Composable () -> Unit = {}
    ) = TopAppBar(modifier = modifier) {
        //val coroutineScope = rememberCoroutineScope()
        WindowDraggableArea {
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            /*scaffoldState?.let {
                                coroutineScope.launch { it.drawerState.open() }
                            }*/
                            onIconClicked()
                        },
                        content = { Icon(Icons.Default.Menu, null) }
                    )
                    menus()
                }

                Text(title, maxLines = 1)

                Row(horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { windowState.isMinimized = true },
                        content = { Icon(Icons.Default.ArrowDropDown, null) }
                    )
                    IconButton(
                        onClick = { windowState.placement = if (windowState.placement == WindowPlacement.Maximized) WindowPlacement.Floating else WindowPlacement.Maximized },
                        content = { Icon(Icons.Default.Add, null) }
                    )
                    IconButton(
                        onClick = { exitProcess(0) },
                        content = { Icon(Icons.Default.Close, null) }
                    )
                }
            }
        }
    }

    interface MMenuScope : ColumnScope {
        // 关闭Menu
        fun collapseMenu()
    }

    @Composable
    fun MMenu(text: String, dropdownMenuItems: @Composable MMenuScope.() -> Unit) {
        var menuExpanded by remember { mutableStateOf(false) }
        Column {
            Text(text, modifier = Modifier.padding(6.dp, 0.dp).clickable { menuExpanded = true; }, maxLines = 1, fontSize = 1.em)
            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }, focusable = true,
                //modifier = Modifier.onPointerEvent(PointerEventType.Exit) { menuExpanded = false }
                ) {
                object : MMenuScope, ColumnScope by this { // 委托 GREAT! C++ 可用using
                    override fun collapseMenu() {
                        menuExpanded = false
                    }
                }.dropdownMenuItems()
            }
        }
    }

    @Composable
    fun MMenuScope.MMenuItem(text: String, onClick: () -> Unit) =
        DropdownMenuItem(
            onClick = { onClick(); collapseMenu() },
            modifier = Modifier.height(28.dp)
        ) {
            Text(text, maxLines = 1)
        }

    @Composable
    fun MMenuScope.MMenuToggle(text: String, checked :Boolean, onClick: (Boolean) -> Unit) =
        DropdownMenuItem(
            onClick = {  },
            modifier = Modifier.height(28.dp)
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(text, maxLines = 1)
                Checkbox(checked,onClick, modifier = Modifier.padding(0.dp).size(14.dp))
            }

        }


    interface MSubmenuScope : RowScope {
        // 关闭Menu
        fun collapseMenu()
    }

    //@OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun MMenuScope.MSubMenu(text: String, dropdownMenuItems: @Composable MSubmenuScope.() -> Unit) {
        var menuExpanded by remember { mutableStateOf(false) }
        DropdownMenuItem(onClick = { menuExpanded = true },modifier = Modifier.height(28.dp)
            //.onPointerEvent(PointerEventType.Enter) { menuExpanded = true }
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(text, maxLines = 1)
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
            }
            Row {
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }, focusable = true,offset = DpOffset(10.dp, (-10).dp)
                    //modifier = Modifier.onPointerEvent(PointerEventType.Exit) { menuExpanded = false }
                    ) {
                    object : MSubmenuScope, RowScope by this@Row {
                        override fun collapseMenu() {
                            this@MSubMenu.collapseMenu()
                        }
                    }.dropdownMenuItems()
                }
            }
        }
    }

    //@OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun MSubmenuScope.MSubMenu(text: String, dropdownMenuItems: @Composable MSubmenuScope.() -> Unit) {
        var menuExpanded by remember { mutableStateOf(false) }
        DropdownMenuItem(onClick = { menuExpanded = true },modifier = Modifier.height(28.dp)
            //.onPointerEvent(PointerEventType.Enter) { menuExpanded = true }
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(text, maxLines = 1)
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, modifier = Modifier.fillMaxHeight())
            }
            Row {
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }, focusable = true, offset = DpOffset(10.dp, (-10).dp)
                    //modifier = Modifier.onPointerEvent(PointerEventType.Exit) { menuExpanded = false }
                ) {
                    object : MSubmenuScope, RowScope by this@Row {
                        override fun collapseMenu() {
                            this@MSubMenu.collapseMenu()
                        }
                    }.dropdownMenuItems()
                }
            }
        }
    }

    @Composable
    fun MSubmenuScope.MMenuItem(text: String, onClick: () -> Unit) =
        DropdownMenuItem(
            onClick = { onClick(); collapseMenu() },
            modifier = Modifier.height(28.dp)
        ) {
            Text(text, maxLines = 1)
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

