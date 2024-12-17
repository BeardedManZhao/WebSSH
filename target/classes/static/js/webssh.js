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
