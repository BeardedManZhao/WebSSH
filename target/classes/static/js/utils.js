class OnOnWebSSH {

    /**
     * 编码 用于将一个数据作加密传输
     * @param data 需要被加密的数据
     * @param urlEncode 是否要使用urlEncode，如果需要加密的数据存储到跳转地址中，则需要设置为 true
     * @returns {string|file}
     */
    static encode(data, urlEncode) {
        const s = btoa(data);
        return urlEncode ? encodeURIComponent(s) : s;
    }

    /**
     * 解码
     * @param data 需要被解密的数据
     * @param urlDecode 是否要使用urlDecode，如果需要解密数据，则需要设置为 true
     * @returns {string|file}
     */
    static decode(data, urlDecode) {
        return urlDecode ? atob(decodeURIComponent(decodeURIComponent(data))) : atob(data);
    }
}