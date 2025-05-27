以下是Vue.js各类修饰符的简明分类介绍及典型用法：

一、表单修饰符（作用于v-model）
‌.lazy‌
输入框失焦后同步数据，减少实时更新频率
<input v-model.lazy="value"> 13

‌.trim‌
自动去除首尾空格
<input v-model.trim="text"> 34

‌.number‌
将输入转为数值类型（无法转换时保留原始值）
<input v-model.number="age"> 14

二、事件修饰符（作用于@event）
‌常用基础修饰符‌

.stop：阻止事件冒泡 12
.prevent：阻止默认行为（如表单提交） 25
.once：仅触发一次 26
html
Copy Code
<button @click.stop.prevent="submit">提交</button>
‌流程控制修饰符‌

.capture：事件捕获模式（从外层向内层触发） 28
.self：仅当事件源自元素本身时触发 16
.passive：立即执行默认行为，优化高频事件（如滚动） 56
‌系统级修饰符‌

.native：监听组件根元素原生事件 3
.sync：语法糖实现父子组件双向绑定 47
三、鼠标/键盘修饰符
‌鼠标按键修饰符‌
.left、.right、.middle限定触发按键
<div @click.right="showMenu">右键菜单</div> 27

‌键盘修饰符‌
.enter、.tab等指定按键触发
<input @keyup.enter="search"> 34

四、特殊属性修饰符（作用于v-bind）
‌.prop‌
强制属性作为DOM property而非attribute绑定
<div v-bind:custom.prop="data"></div> 14

‌.camel‌
将短横线属性名转为驼峰式（兼容JS变量命名）
<svg :view-box.camel="values"> 17

注意事项
修饰符组合时执行顺序从左到右
@click.prevent.self（先阻止默认行为再判断事件源）≠ @click.self.prevent 18
冲突修饰符不可混用（如.prevent与.passive） 6
通过合理使用修饰符，可显著简化事件处理逻辑并优化交互性能。