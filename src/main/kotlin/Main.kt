// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the GNU Affero General Public License that can be found in the LICENSE file.
import MTopMenuBar.MMenu
import MTopMenuBar.MMenuBar
import MTopMenuBar.MMenuItem
import MTopMenuBar.MMenuToggle
import MTopMenuBar.MSubMenu
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import com.github.kittinunf.fuel.httpGet
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.awt.Desktop
import java.awt.FileDialog
import java.io.File
import java.net.URI
import java.nio.charset.Charset
import javax.swing.*
import javax.swing.UIManager.getLookAndFeel


val ThirdColor = Color(0xFFBB86FC)
val SecondaryColor = Color(0xFF03DAC5)
val MainColor = Color(0xFF1e88a8) // 花浅葱


@Deprecated("", replaceWith = ReplaceWith("this && Unit==x"))
infix fun <T> Boolean.thenDo(x: T): T? = if (this) {
    x
} else {
    null
}


@Composable
fun ColorChooserCard() {
    val defaultColors = mapOf<String, Color>(
        "枫叶红" to Color(0xFFc21f30),
        "李紫" to Color(0xFF2b1216),
        "青葱" to Color(0xff00a3af)
    )

    var colour by remember { mutableStateOf(defaultColors.entries.first().toPair()) }

    CardColumn("颜色选择") {
        Canvas(
            modifier = Modifier.size(100.dp).clip(RoundedCornerShape(10.dp)),
            onDraw = {
                drawRect(color = colour.second)
            }
        )
        //Box(Modifier.size(100.dp).background(colour.second))
        defaultColors.forEach { (k, v) ->
            Row(modifier = Modifier.fillMaxHeight(),verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = k == colour.first,{colour= Pair(k,v) }, colors = RadioButtonDefaults.colors(v))
                Text(buildAnnotatedString {
                    pushStyle(SpanStyle(
                        color = v,
                        fontSize = 16.sp,
                        fontStyle = FontStyle.Normal
                    ))
                    append(k)
                    pop()
                },Modifier.clickable { colour = Pair(k,v) })
            }
        }
    }


}

@Composable
fun ImgCard() {
    var img: Painter? by remember { mutableStateOf(null) }
    fun painterStorage(path: String): BitmapPainter = File(path).inputStream().buffered().use { BitmapPainter(loadImageBitmap(it)) }

    CardColumn("图片查看器") {
        AnimatedVisibility(img != null) {
            when (img) {
                null -> Text("No image available")
                else -> Image(
                    painter = img!!,
                    contentDescription = "Sample Img",
                    modifier = Modifier.fillMaxSize().animateContentSize()
                )
            }
        }
        Button(onClick = {
            img = FileDialog(ComposeWindow(), "选择图片").apply {
                mode = FileDialog.LOAD
                file = "*.jpg;*.jpeg;*.png;*.gif;*.bmp"
                //filenameFilter = FilenameFilter { _, name -> name.matches(Regex("\\S+.(jpg|png|jpeg|gif)]"))  }
                isVisible = true
            }.run {
                if (directory != null && file != null)
                    painterStorage(directory + file)
                else
                    null
            }
        }) {
            Text("File Picker")
        }
        Button(onClick = { img = null }) { Text("Clear") }
    }
}

@Composable
fun <T> ImgCardAsync() {
    val img by produceState<T?>(null) { //副作用
        value = null
    }
    CardColumn {
        when (img) {
            null -> {}
            else -> {}
        }
    }
}


/*
* useEffect is used for many purposes: event subscriptions, logging, asynchronous code, and more.
* Compose breaks up these use cases into separate functions with the suffix Effect, including DisposableEffect, LaunchedEffect, and SideEffect.

Run when id changes
LaunchedEffect(id)
useEffect(() => {}, [id]);

SideEffect
Run every render
useEffect(() => {});

LaunchedEffect(Unit)
Run on the first render
useEffect(() => {}, []);
*/

@Composable
fun JvmInfoCard() {
    val getInfo: (String) -> String = { System.getProperty(it) }

    Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Text("Kotlin ${KotlinVersion.CURRENT}")

            //Text("JDK: ${getInfo("java.version")} by ${getInfo("java.vendor")}")
            Text("JVM: ${getInfo("java.vm.name")} ${Runtime.version()} by ${getInfo("java.vm.vendor")} at ${getInfo("java.home")}")
            Text("OS:  ${getInfo("os.name")} ${getInfo("os.arch")} ${getInfo("os.version")}")
            Text("Charset: Default=${Charset.defaultCharset()} File=${getInfo("file.encoding")}")
            Text("Swing LookAndFeel: ${getLookAndFeel().description}")
        }
    }

}

/*
object SysTrayIconImg : Painter() {
    override val intrinsicSize = Size(256f, 256f)

    override fun DrawScope.onDraw() {
        drawOval(Color(0xFFFFA500))
    }
}
 */

@Composable
fun TimerCard() {
    var time by remember { mutableStateOf(0) }

    LaunchedEffect(time) {
        delay(1000L)
        time = (time + 1) % 60
    }

    CardColumn("计时器") {
        Text("$time")
        Button(onClick = { time = 0 }) {
            Text("Reset")
        }
    }
}

/*
suspend fun alignTime(precision :Long = 20L, callback: () -> Unit) {
    var current: LocalTime
    while (true) {
        delay(precision / 5)
        current = LocalTime.now()
        val timeMiles: Int = (current.nano / 100000)
        if (timeMiles < precision || timeMiles > 1000 - precision) {
            break
        }
    }
    callback()
}


@Composable
fun DateTimeCard() {
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    var timeAligned by remember { mutableStateOf(false) }
    val precision = 20L


    LaunchedEffect(now) {
        delay(1000L)
        val current = LocalDateTime.now()
        now = if (current.nano> 1e6*500) current.plusSeconds(1) else current
        if(timeAligned) {
            if(now.nano > 1e6*precision && now.nano < 1e6*(1000-precision)) {
                timeAligned = false
            }
        }
    }

    val scope = rememberCoroutineScope()
    scope.launch {
        while(!timeAligned) {
            alignTime(precision) {
                now = LocalDateTime.now()
                timeAligned = true
            }
        }
    }

    CardColumn {
        Text(now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")))
        Text( " ${now.nano / 1e6} ms " + if (timeAligned) "已对齐" else "未对齐")
    }
}

*/

@Composable
fun MaterialIconsGalleryCard() {
    val isWindowVisible = mutableStateOf(false)
    val testString = mutableStateOf("Hello")
    CardColumn {
        OutlinedTextField(label = { Text("testString") }, value = testString.value, onValueChange = { testString.value = it })
        Button(onClick = {
            isWindowVisible.value = true
        }) {
            Text("新窗口")
        }

        InputWindowTest(isWindowVisible, testString)
    }
}

@Composable
fun InputWindowTest(isWindowVisible: MutableState<Boolean>, testString: MutableState<String>) {
    var isWindowVisible by remember { isWindowVisible }
    var testString by remember { testString }

    MaterialTheme(colors = if (isSystemInDarkTheme()) darkColors(MainColor, SecondaryColor, ThirdColor) else lightColors(MainColor, SecondaryColor, ThirdColor)) {
        Window(visible = isWindowVisible, onCloseRequest = { isWindowVisible = false }, title = "composePlay") {
            CardColumn {
                OutlinedTextField(label = { Text("testString") }, value = testString, onValueChange = { testString = it })
                Row {
                    Icon(Icons.Default.Notifications, null)
                    Icon(Icons.Default.LocationOn, null)
                    Icon(Icons.Default.Lock, null)
                    Icon(Icons.Default.CheckCircle, null)
                }

            }
        }
    }
}

@Composable
fun FuelInternetCard() {
    //val serverURL = "http://39.105.184.209:81/api/storage?name=sdd&password=nitingxiua"
    //val (resp, setResp) = remember { mutableStateOf("Hello, World!") }

    CardColumn("网络") {
            /*
            Button(onClick = {
                Fuel.get(serverURL)
                    .response { result ->
                        result.fold(
                            { data -> setResp(data.toString(Charset.forName("utf-8"))); },
                            { error -> println(error.message) }
                        )
                    }
            })
            {
                Text("Fuel Get")
            }
            Text(resp)
            */
            val (hasInternetAccess, setHasInternetAccess) = remember { mutableStateOf(false) }
            Button(onClick = { testInternetAccess { setHasInternetAccess(it) } }) {
                Text("测试互联网连接")
            }
            Text(if (hasInternetAccess) "OK" else "No Access")
        }
}


@Serializable
data class UselessFactJsonModel(
    val id: String,
    val language: String,
    val permalink: String,
    val source: String,
    val source_url: String,
    val text: String
)

enum class InternetStatus {
    Fetching,
    OK,
    Error,
}

@Composable
@Preview
fun UselessFactCard() {
    val url = "https://uselessfacts.jsph.pl/random.json"
    var info by remember { mutableStateOf(UselessFactJsonModel("", "", "", "", "", "")) }
    var status by remember { mutableStateOf(InternetStatus.Fetching) }

    fun updateUselessFact() {
        status = InternetStatus.Fetching
        url.httpGet().response { result ->
            result.fold(
                { data -> info = Json.decodeFromString(data.toString(Charset.forName("utf-8"))); status = InternetStatus.OK; },
                { err -> status = InternetStatus.Error; println(err) }
            )
        }
    }

    LaunchedEffect(Unit) {
        updateUselessFact()
    }

    Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp).animateContentSize()) {
            Text("网络请求", fontSize = 1.7.em)
            Divider()
            Spacer(Modifier.padding(4.dp))
            when (status) {
                InternetStatus.Fetching -> {
                    CircularProgressIndicator()
                }
                InternetStatus.OK -> {
                    Text(info.text, fontFamily = FontFamily.Cursive, fontSize = 1.5.em)
                    Text("source: ${info.source} ${info.source_url}", fontStyle = FontStyle.Italic)
                    Button(onClick = ::updateUselessFact) {
                        Text("Refresh")
                    }
                }
                else -> {
                    Text("Error")
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AlertDialogCard() {
    var msgBoxOpen by remember { mutableStateOf(false) }

    CardColumn("消息框") {
        Button(onClick = { msgBoxOpen = true }) {
            Text("Compose 消息框")
        }

        msgBoxOpen && Unit == AlertDialog(
            onDismissRequest = { msgBoxOpen = false },
            title = { Text("测试标题") },
            text = { Text("测试") },
            confirmButton = {
                TextButton(onClick = { msgBoxOpen = false }) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { msgBoxOpen = false }) {
                    Text("取消")
                }
            }
        )

        Button({JOptionPane.showMessageDialog(ComposeWindow(),"Msg")}) {
            Text("Swing 消息框")
        }
    }
}


@Composable
fun ListCard() {
    val listData = remember {  mutableStateListOf("Kotlin", "C/C++", "JS") }

    LaunchedEffect(Unit) {
        flow {
            for (i in 1..30) {
                delay(1000) // 假装我们在这里做了一些有用的事情
                emit(i) // 发送下一个值
            }
        }.collect {
            listData.add("$it")
        }
    }

    CardColumn("Kotlin Flow") {
        listData.forEachIndexed { index, s ->
            Text("$index: $s")
        }
    }
}

@Composable
fun SwingCard() {
    CardColumn("Swing") {
        SwingPanel(
            background = Color.White,
            modifier = Modifier.size(270.dp, 90.dp),
            factory = {
                JPanel().apply {
                    layout = BoxLayout(this, BoxLayout.Y_AXIS)
                    val txt = JTextField("Hello").also(::add)
                    JButton("Button").apply {
                        addActionListener {
                            txt.text = "clicked!"
                        }
                    }.also(::add)
                }
            }
        )
    }
}


@Composable
fun CardColumn(text: String? = null,content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp).animateContentSize()) {
            if(text != null) {
                Text(text, fontSize = 1.7.em)
                Divider()
                Spacer(Modifier.padding(4.dp))
            }

            content()
        }
    }
}

//@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun AppPage() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()))
/*
        ContextMenuDataProvider(items = {
            listOf(
                ContextMenuItem("Exit") { exitProcess(0) }
            )
        })
*/
    {
        ColorChooserCard()
        ImgCard()
        UselessFactCard()
        TimerCard()
        FuelInternetCard()
        AlertDialogCard()
        ListCard()
        SwingCard()
    }
}

fun testInternetAccess(testUrl: String = "http://wifi.vivo.com.cn/generate_204", callback: (Boolean) -> Unit) {
    testUrl.httpGet().response { _, response, _ ->
        callback(response.statusCode == 204)
    }
}


@Composable
fun AboutPage() {
    JvmInfoCard()
}

enum class Pages {
    HOME,
    ABOUT
}


fun mainApp() = application {
    val windowState = rememberWindowState(size = DpSize(1024.dp, 768.dp), position = WindowPosition(100.dp, 40.dp))
    val trayState = rememberTrayState()
    val notificationState = rememberNotification("title", "message", Notification.Type.Info)

    //System.getProperties()["file.encoding"] = "GBK";
    Tray(
        state = trayState,
        icon = rememberVectorPainter(Icons.Default.Home),
        tooltip = "Compose App Demo 中文",
        menu = {
            Item(
                "Show",
                onClick = { windowState.isMinimized = false }
            )
            Item(
                "退出",
                onClick = ::exitApplication
            )
            Item(
                "Notify",
                onClick = { trayState.sendNotification(notificationState) }
            )
        },
        onAction = { windowState.isMinimized = false }
    )


    Window(onCloseRequest = ::exitApplication, title = "composePlay", state = windowState, undecorated = true, transparent = true) {

        /*
        MenuBar {
            Menu("File", mnemonic = 'F') {
                Item("Test", onClick = { })
                Item("Exit", onClick = ::exitApplication) // this@application.exitApplication() 将函数转为lambda表达式类型
            }
        }
        */

        MaterialTheme(colors = if (isSystemInDarkTheme()) darkColors(MainColor, SecondaryColor, ThirdColor) else lightColors(MainColor, SecondaryColor, ThirdColor)) {
            val scaffoldState = rememberScaffoldState()
            var pagination by remember { mutableStateOf(Pages.HOME) }
            val coroutineScope = rememberCoroutineScope()
            var sideBarVisible by remember {mutableStateOf(false)}

            Scaffold(
                scaffoldState = scaffoldState,
                modifier = Modifier.clip(RoundedCornerShape(5.dp)), //.border(BorderStroke(2.dp, MaterialTheme.colors.primary))
                topBar = {
                    MMenuBar("Jetpack Compose Demo", windowState, {sideBarVisible = !sideBarVisible}) {
                        MMenu("文件") {
                            MMenuItem("新建") {
                                collapseMenu()
                                JOptionPane.showMessageDialog(ComposeWindow(),"Msg")
                            }
                            MMenuItem("打开") {
                                pagination = Pages.HOME
                            }
                            Divider()
                            MMenuItem("退出") {
                                exitApplication()
                            }
                        }
                        MMenu("设置")
                        {
                            MMenuItem("编辑") {
                                pagination = Pages.ABOUT
                            }
                            MMenuItem("首选项") {
                            }
                        }
                        MMenu("帮助")
                        {
                            MMenuItem("帮助") {
                                pagination = Pages.ABOUT
                            }
                            MMenuItem("关于") {
                                pagination = Pages.ABOUT
                            }

                            var toggledTest by remember { mutableStateOf(false) }
                            MMenuToggle("测试",toggledTest) {
                                println(it)
                                toggledTest = !toggledTest
                            }

                            MSubMenu("展开") {
                                MMenuItem("1") {
                                    println("Hello")
                                }
                                MSubMenu("展开") {
                                    MMenuItem("3") {
                                        println("Hello")
                                    }
                                    MMenuItem("4") {
                                        println("Hello")
                                    }
                                }
                                MMenuItem("3") {
                                    println("Hello")
                                }
                                MMenuItem("4") {
                                    println("Hello")
                                }
                            }

                            // custom menu item
                            DropdownMenuItem(onClick = { collapseMenu(); Desktop.getDesktop().browse(URI("https://github.com/JetBrains/compose-jb/")) },modifier = Modifier.height(28.dp)) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                    Text("官网")
                                    Icon(Icons.Default.Share,null, modifier = Modifier.fillMaxHeight())
                                }
                            }

                        }

                    }
                },
                bottomBar = {
                    // can also use BottomNavigation
                    BottomAppBar(modifier = Modifier.height(18.dp)) {
                        Text(text = "Location: ${windowState.position} Size: ${windowState.size}")
                    }
                },
                content = {
                    Row {
                        sideBarVisible && Unit == Box(Modifier.fillMaxHeight().width(40.dp).background(MainColor)) { //侧边栏
                            Column(Modifier.fillMaxSize(),verticalArrangement = Arrangement.SpaceBetween,horizontalAlignment = Alignment.CenterHorizontally) {
                                Column {
                                    Spacer(Modifier.height(10.dp))
                                    Icon(Icons.Default.Face, null)
                                    Icon(Icons.Default.Favorite, null)
                                    Icon(Icons.Default.AccountBox, null)
                                }

                                Column {
                                    Icon(Icons.Default.ExitToApp, null)
                                    Spacer(Modifier.height(20.dp))
                                }

                            }
                        }

                        when (pagination) {
                            Pages.HOME -> AppPage()
                            Pages.ABOUT -> AboutPage()
                        }

                    }
                }
            )
        }
    }
}

fun main(args :Array<String>) {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    mainApp()
}


