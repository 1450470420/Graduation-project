const { announcementApi } = require('../../utils/api')
Page({ data:{items:[]}, onLoad(){this.load()}, async load(){try{const r=await announcementApi.getList();this.setData({items:r.data})}catch(e){}} })
