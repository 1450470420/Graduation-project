const { chatApi } = require('../../utils/api')
Page({
  data: { messages: [], content: '', orderId: null, myId: null, receiverId: null, scrollId: '' },

  onLoad(o) {
    const app = getApp()
    this.setData({
      orderId: o.orderId,
      receiverId: Number(o.receiverId || 0),
      myId: app.globalData.userInfo ? app.globalData.userInfo.id : null
    })
    this.loadHistory()
  },

  onShow() {
    this.startPolling()
  },

  onHide() {
    this.stopPolling()
  },

  onUnload() {
    this.stopPolling()
  },

  async loadHistory() {
    try {
      const r = await chatApi.getHistory(this.data.orderId)
      const msgs = r.data || []
      this.setData({ messages: msgs })
      this._lastId = msgs.length > 0 ? msgs[msgs.length - 1].id : 0
      this.scrollToBottom()
    } catch (e) {}
  },

  scrollToBottom() {
    const msgs = this.data.messages
    if (msgs && msgs.length > 0) {
      this.setData({ scrollId: 'msg-' + msgs[msgs.length - 1].id })
    }
  },

  startPolling() {
    if (this._pollTimer) return
    this._pollTimer = setInterval(() => {
      this.pollNewMessages()
    }, 3000)
  },

  stopPolling() {
    if (this._pollTimer) {
      clearInterval(this._pollTimer)
      this._pollTimer = null
    }
  },

  async pollNewMessages() {
    try {
      const r = await chatApi.getNewMessages(this.data.orderId, this._lastId || 0)
      if (r.data && r.data.hasNew && r.data.messages.length > 0) {
        const newMsgs = r.data.messages
        const allMsgs = this.data.messages.concat(newMsgs)
        this._lastId = newMsgs[newMsgs.length - 1].id
        this.setData({ messages: allMsgs })
        this.scrollToBottom()
      }
    } catch (e) {}
  },

  async sendMsg() {
    if (!this.data.content.trim()) return
    try {
      await chatApi.send({ orderId: this.data.orderId, receiverId: this.data.receiverId, content: this.data.content })
      this.setData({ content: '' })
      this.pollNewMessages()
    } catch (e) {}
  },

  onInput: function(e) { var d = {}; d[e.currentTarget.dataset.field] = e.detail.value; this.setData(d) }
})
