### 365 Done
**一款基于 GTD 开发的日程事务管理 APP，也是我的毕设，侥幸获得了校级和市级优秀毕设，不过我自己看来其实还是很粗糙的，有机会尽可能优化下吧**

#### 目前的功能
1.  集成科大讯飞语音听写功能
2.  建立多种在应用中接受分享内容的通道
3.  按照月、周、日等日历视图展示备忘事项
4.  按照文件夹分类展示备忘事项
5.  农历日历的显示
6.  个性化节假日设定

> 方便看到的朋友直接下载使用，我没有隐藏讯飞的 KEY，目前这个因为只使用了讯飞的语音听写，这个功能暂时还是免费的，所以就无所谓了，代码的使用只要遵从开源协议即可。

#### 项目特点
说起来虽然这个项目获得了优秀毕设，但从工程上将还是不完善的，MVP架构也只有一部分的 Activity 用了，剩下的因为在初期没有设计好，要重构也有点麻烦，所以暂时就放着了。

整个项目用到最多的设计模式应该就是单例模式了，其次是观察者模式，以及严格上不算数的工厂方法模式。

观察者模式和单例模式结合实现了 App 内部的消息传递机制，作用类似于 EventBus，不过因为是自己写的，使用和理解还是挺方便的，可能因为业务并不太复杂，也没有什么并发处理，目前还没有出现传递混乱消息丢失或并发问题。

然后用单例封装了 Toast 和 SharedPreference，这基本上是我每个项目里的标配了。封装 Toast 使 App 全局 仅维护一个 Toast 实例，这样不会出现 Toast 延时和重叠的问题。SharedPreference 封装就是更加方便使用了，不用每次需要的的时候都重新实例化，当然跟前面的消息机制一样，没有特别考虑并发处理方面。

### App 截图
![](https://github.com/alphagao1993/365-Done/blob/master/images/01.png)
![](https://github.com/alphagao1993/365-Done/blob/master/images/02.png)
![](https://github.com/alphagao1993/365-Done/blob/master/images/03.png)
![](https://github.com/alphagao1993/365-Done/blob/master/images/04.png)
![](https://github.com/alphagao1993/365-Done/blob/master/images/05.png)
![](https://github.com/alphagao1993/365-Done/blob/master/images/06.png)
![](https://github.com/alphagao1993/365-Done/blob/master/images/07.png)
![](https://github.com/alphagao1993/365-Done/blob/master/images/08.png)
![](https://github.com/alphagao1993/365-Done/blob/master/images/09.png)

### TODO
有几个功能毕设期间就像加进去，不过当时相对毕设已经足够复杂了，而来当时与朋友创业做另一个项目时间并不算富裕，也就没有继续开发了。
- 集成云存储
    + 有了云存储就不怕数据丢失了
- 地图顶点提醒
    + 就是苹果待办里面的到达某个地点进行提醒

暂时就想到这两点，对于 UI 我是没办法了，丑就丑吧，毕竟我一个工科男虽然知道什么叫好看，但是自己独立做不出好看的。。