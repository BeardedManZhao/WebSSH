class WSSHClient {
    constructor() {
        console.info("                  .-') _                   .-') _ \s\n                 ( OO ) )                 ( OO ) )\s\n .-'),-----. ,--./ ,--,'  .-'),-----. ,--./ ,--,' \s\n( OO'  .-.  '|   \\ |  |\\ ( OO'  .-.  '|   \\ |  |\\ \s\n/   |  | |  ||    \\|  | )/   |  | |  ||    \\|  | )\s\n\\_) |  |\\|  ||  .     |/ \\_) |  |\\|  ||  .     |/ \s\n  \\ |  | |  ||  |\\    |    \\ |  | |  ||  |\\    |  \s\n   `'  '-'  '|  | \\   |     `'  '-'  '|  | \\   |  \s\n     `-----' `--'  `--'       `-----' `--'  `--'  \s\n                     welcome to OnOn-WebSSH~~~~~");
        this.isBinarySend = false;
    }

    static getSplitChar() {
        return ',';
    }

    setUUID(uuid) {
        this._uuid = uuid;
    }

    getUUID() {
        return this._uuid;
    }

    _generateEndpoint() {
        const protocol = window.location.protocol === 'https:' ? 'wss://' : 'ws://';
        return protocol + window.location.host + '/webssh';
    }

    connect(options) {
        const endpoint = this._generateEndpoint();
        if (window.WebSocket) {
            this._connection = new WebSocket(endpoint);
        } else {
            options.onError('WebSocket Not Supported');
            return;
        }

        this._connection.onopen = (event) => {
            options.onConnect();
        };

        this._connection.onmessage = (evt) => {
            const data = evt.data.toString();
            if (data.startsWith("OnOnWebSsh-20030806-")) {
                const s = data.substring("OnOnWebSsh-20030806-".length, data.length);
                if (s.startsWith("show_uuid")) {
                    this.setUUID(s.substring("show_uuid".length, s.length));
                    console.info(this.getUUID());
                }
                switch (s) {
                    case "sftp_upload":
                        if (this.isBinarySend) {
                            this.isBinarySend = false;
                            // 调出控制台
                            this.sendClientData("\r\n");
                        } else {
                            this.isBinarySend = true;
                            options.onData("文件开始上传，请耐心等待!!!\r\n");
                        }
                        break;
                    default:
                        break;
                }
                return;
            }
            options.onData(data);
        };

        this._connection.onclose = () => {
            options.onClose();
        };
    }

    send(data) {
        this._connection.send(JSON.stringify(data));
    }

    sendInitData(options) {
        this._connection.send(JSON.stringify(options));
    }

    sendClientData(data, errorCallback) {
        if (data instanceof Blob) {
            this._connection.send(data);
        } else {
            if (this.isBinarySend) {
                errorCallback("有文件正在传输中，请等待文件传输结束！\r\n");
                return;
            }
            this._connection.send(JSON.stringify({operate: "command", command: data}));
        }
    }

    /**
     * 文件上传
     * @param file 需要被上传的文件
     * @param wordDir 被上传文件要存储在哪里
     */
    async uploadFile(file, wordDir) {
        if (!file) {
            return;
        }
        const formData = new FormData();
        formData.append('file', file);
        formData.append('wordDir', wordDir);
        formData.append('uuid', this.getUUID());

        try {
            await fetch('/upload', {
                method: 'POST',
                body: formData
            });
        } catch (error) {
            console.error('Error:', error);
        }
    }

    download(filePath) {
        const number = filePath.lastIndexOf('/');
        const fileName = filePath.substring(number + 1);
        const dirPath = filePath.substring(0, number);
        window.open(`download?uuid=${this.getUUID()}&dirPath=${dirPath}&fileName=${fileName}`);
    }
}


// 尝试搜索 url 中的参数
const params = {};
params.operate = "connect";
window.location.search.substring(1).split("&").forEach(function (item) {
    const kv = item.split("=");
    params[kv[0]] = kv[1];
});
if (params.password) {
    params.password = OnOnWebSSH.decode(params.password, window.location.href.concat("noUrlCode"));
}

function extracted(numberKey, defaultValue, stringValue) {
    if (params[numberKey]) {
        return;
    }
    const s = prompt(stringValue, defaultValue);
    if (s) {
        params[numberKey] = s;
    } else {
        alert("参数不能为空");
        extracted(numberKey, defaultValue, stringValue);
    }
}

extracted("host", "127.0.0.1", "请输入您要连接的主机IP/域名！");
extracted("port", "22", `请输入您要连接到 ${params.host} 的ssh端口`);
extracted("username", "root", `请输入您要连接到 ${params.host}:${params.port} 的用户名`);
extracted("password", "123456", `请输入您要连接到 ${params.username}@${params.host} 的密码~`);

// 开始启动
openTerminal(params);

function openTerminal(options) {
    const client = new WSSHClient();
    const term = new Terminal({
        cursorBlink: true, // 光标闪烁
        cursorStyle: "bar", // 光标样式  null | 'block' | 'underline' | 'bar'
        scrollback: 800, //回滚
        tabStopWidth: 8, //制表宽度
        screenKeys: true
    });

    term.on('data', function (data) {
        //键盘输入时的回调函数
        client.sendClientData(data, e => term.write(e));
    });
    term.open(document.getElementById('terminal'));

    //在页面上显示连接中...
    term.write('Connecting...\r\n');
    //执行连接操作
    client.connect({
        onError: function (error) {
            //连接失败回调
            term.write('Error: ' + error + '\r\n');
        },
        onConnect: function () {
            //连接成功回调
            client.sendInitData(options);
        },
        onClose: function () {
            //连接关闭回调
            term.write("\rconnection closed\r\n");
            term.write("\rReturn to the previous page after 5 seconds.\r\n");
            let s = 4;
            setInterval(() => {
                term.write(`\rReturn to the previous page after ${s} seconds.\r\n`);
                if (s-- <= 0) {
                    if (window.history.length > 1) {
                        history.back();
                    } else {
                        window.location.href = '/index.html';
                    }
                }
            }, 1000);
        },
        //收到数据时回调
        onData: function (data) {
            term.write(data);
            term.scrollToBottom();
        }
    });

    document.querySelector("#buttonList_sendFile").addEventListener("click", function (_) {
        const input = document.createElement("input");
        input.type = "file";
        input.multiple = true;
        input.onchange = function () {
            const files = input.files;

            if (!files || files.length === 0){
                return;
            }
            let s = prompt(`您要将选中的 ${files.length} 个文件上传到哪个目录？`);
            if (!s) {
                return;
            }
            if (!s.endsWith('/')) {
                s += '/';
            }
            let index = 0;
            const interval = setInterval(() => {
                if (index === files.length){
                    clearInterval(interval);
                    // 调出命令行
                    client.sendClientData("\r\n");
                    document.querySelector(".xterm").focus();
                    return;
                }
                const file = files[index++];
                // 调用 结果会追加到 session 的回复 这里不需要处理结果！
                client.uploadFile(file, s);
            }, 100);
        };
        input.click();
    })

    document.querySelector("#buttonList_downFile").addEventListener("click", function (_) {
        let s = prompt(`您要将下载哪个文件？（输入路径）`);
        if (!s) {
            return;
        }
        client.download(s);
        // 焦点离开
        document.querySelector(".xterm").focus();
    })

    function resizeTerminal() {
        const xterm_rows = document.getElementsByClassName('xterm-rows')[0];
        const terminal = document.getElementById('terminal');

        // 获取字体大小和行高
        const fontSize = 10;
        const lineHeight = 18;

        // 计算列数和行数
        const cols = Math.floor(xterm_rows.clientWidth / fontSize);
        const rows = Math.floor(terminal.clientHeight / lineHeight);
        console.log(`cols: ${cols}, rows: ${rows}`);
        term.resize(term.cols, rows);

        document.querySelector(".xterm-viewport").style.height = '100vh';
    }

    // 初始化时调用一次
    resizeTerminal();
}