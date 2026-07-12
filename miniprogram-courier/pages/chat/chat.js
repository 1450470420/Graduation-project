const { chatApi } = require('../../utils/api')
Page({
  data: { messages: [], content: '', orderId: null, myId: null, receiverId: null, scrollId: '' },

  onLoad: function(o) {
    var app = getApp()
    var myId = app.globalData.userInfo ? app.globalData.userInfo.id : null
    this.setData({ orderId: o.orderId, receiverId: Number(o.receiverId || 0), myId: myId })
    this.load()
  },

  onShow: function() {
    this.startPolling()
  },

  onHide: function() {
    this.stopPolling()
  },

  onUnload: function() {
    this.stopPolling()
  },

  load: async function() {
    try {
      var r = await chatApi.getHistory(this.data.orderId)
      var msgs = r.data || []
      this.setData({ messages: msgs })
      this._lastId = msgs.length > 0 ? msgs[msgs.length - 1].id : 0
      this.scrollToBottom()
    } catch(e) {}
  },

  scrollToBottom: function() {
    var msgs = this.data.messages
    if (msgs && msgs.length > 0) this.setData({ scrollId: 'msg-' + msgs[msgs.length - 1].id })
  },

  startPolling: function() {
    if (this._pollTimer) return
    var that = this
    this._pollTimer = setInterval(function() {
      that.pollNewMessages()
    }, 3000)
  },

  stopPolling: function() {
    if (this._pollTimer) {
      clearInterval(this._pollTimer)
      this._pollTimer = null
    }
  },

  pollNewMessages: async function() {
    try {
      var r = await chatApi.getNewMessages(this.data.orderId, this._lastId || 0)
      if (r.data && r.data.hasNew && r.data.messages.length > 0) {
        var newMsgs = r.data.messages
        var allMsgs = this.data.messages.concat(newMsgs)
        this._lastId = newMsgs[newMsgs.length - 1].id
        this.setData({ messages: allMsgs })
        this.scrollToBottom()
      }
    } catch(e) {}
  },

  send: async function() {
    if (!this.data.content.trim()) return
    try {
      await chatApi.send({ orderId: this.data.orderId, receiverId: this.data.receiverId, content: this.data.content })
      this.setData({ content: '' })
      this.pollNewMessages()
    } catch(e) {}
  },

  onInput: function(e) { var d = {}; d[e.currentTarget.dataset.field] = e.detail.value; this.setData(d) }
})
