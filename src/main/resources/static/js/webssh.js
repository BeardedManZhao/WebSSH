function WSSHClient() {
}

WSSHClient.prototype._generateEndpoint = function () {
    const protocol = window.location.protocol === 'https:' ? 'wss://' : 'ws://';
    return protocol + window.location.host + '/webssh';
};

WSSHClient.prototype.connect = function (options) {
    const endpoint = this._generateEndpoint();

    if (window.WebSocket) {
        //如果支持websocket
        this._connection = new WebSocket(endpoint);
    } else {
        //否则报错
        options.onError('WebSocket Not Supported');
        return;
    }

    this._connection.onopen = function () {
        options.onConnect();
    };

    this._connection.onmessage = function (evt) {
        const data = evt.data.toString();
        //data = base64.decode(data);
        options.onData(data);
    };


    this._connection.onclose = function () {
        options.onClose();
    };
};

WSSHClient.prototype.send = function (data) {
    this._connection.send(JSON.stringify(data));
};

WSSHClient.prototype.sendInitData = function (options) {
    //连接参数
    this._connection.send(JSON.stringify(options));
}

WSSHClient.prototype.sendClientData = function (data) {
    //发送指令
    this._connection.send(JSON.stringify({"operate": "command", "command": data}))
}