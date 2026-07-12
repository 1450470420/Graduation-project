const { messageApi } = require('../../utils/api')
Page({ data:{messages:[]}, onShow(){this.load()}, async load(){try{const r=await messageApi.getList();this.setData({messages:r.data})}catch(e){}} })
