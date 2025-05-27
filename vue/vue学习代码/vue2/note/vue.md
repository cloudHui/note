# 1. 谷歌极简插件下载  https://chrome.zzzmh.cn/#/search



#在返回的所有 url 地址 如果是本地的不用加 https://  如果是其他位置的需要加这个不然就会访问本地的地址



enumerable:true，//控制属性是否可以枚举，默认值是false

writable:true，//控制属性是否可以被修改，默认值是false

configurable:true //控制属性是否可以被删除，默认值是



1. ‌var‌：老式声明，会变量提升（声明前可用），函数作用域，可重复声明

2. ‌let‌：ES6新语法，块级作用域（{}内有效），不能重复声明，声明前用会报错

3. ‌const‌：和let类似，但声明后必须赋值，且不能重新赋值（常量）

👉 简单记：用let代替var，不变的量用const。



块内方法可以省略 :function 方法名
要使用:onclick="yourFunction($event,name)",这个那么可以是 vue定义的值

事件的基本使用：

  1.使用v-on：xxx或@xxx绑定事件，其中xxx是事件名；

  2.事件的回调需要配置在methods对象中，最终会在vm上；

  3.methods中配置的函数，不要用箭头函数！否则this就不是vm了；

  4.methods中配置的函数，都是被Vue所管理的函数，this的指向是vm或组件实例对象；

  5.@click="demo”和@click="demo（$event）”效果一致，但后者可以传参；

  Vue中的事件修饰符：可以连续使用  @click.prevent.stop
    1.prevent：阻止默认事件(只调用函数不根据标签的作用处理)（常用
    2.stop：阻止事件冒泡(事件不会往上层传递只限定在这一层如果还有上上层需要在上层也stop)（常用）；
    3.once：事件只触发一次（常用）
    4.capture：使用事件的捕获模式；
    5.self：只有event.target是当前操作的元素是才触发事件；
    6.passive：事件的默认行为立即执行，无需等待事件回调执行完毕；

滚轮事件（@wheel）‌
→ 由鼠标滚轮/触摸板触发，‌无论是否产生滚动结果(就是滚轮滚动了页面是否面便)都会触发，可获取滚动方向(deltaY/deltaX)。

‌滚动条事件（@scroll）‌
→ 由滚动条位置变化触发（含滚轮/拖动/键盘等），‌仅实际滚动时触发‌，常用于监听滚动位置
