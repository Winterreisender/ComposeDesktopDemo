import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import kotlin.system.exitProcess

object MyTopMenuBar {
    @Composable
    fun WindowScope.MyMenuBar(
        title :String,
        windowState: WindowState,
        //scaffoldState :ScaffoldState,
        modifier :Modifier = Modifier.height(32.dp),
        menus: @Composable () -> Unit = {}
    ) = TopAppBar(modifier = modifier) {
        //val coroutineScope = rememberCoroutineScope()
        WindowDraggableArea {
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {/*coroutineScope.launch {scaffoldState.drawerState.open()}*/
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
                        onClick = { windowState.placement = if(windowState.placement == WindowPlacement.Maximized) WindowPlacement.Floating else WindowPlacement.Maximized },
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

    interface MyMenuScope :ColumnScope {
        fun collapse()
    }
    @Composable
    fun MyMenu(text :String, dropdownMenuItems :@Composable MyMenuScope.()->Unit) {
        var menuExpanded by remember { mutableStateOf(false) }
        Column {
            Text(text, modifier = Modifier.padding(6.dp,0.dp).clickable { menuExpanded = true ; } ,maxLines = 1 )

            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }, focusable = true) {
                object: MyMenuScope, ColumnScope by this { // 委托 GREAT! C++ 可用using
                    override fun collapse() {
                        menuExpanded = false;
                    }
                }.dropdownMenuItems()
            }
        }
    }

    @Composable
    fun MyMenuScope.MyMenuItem(text :String,onClick :()->Unit) = DropdownMenuItem(onClick = {onClick(); collapse()}) {
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

