1.Vue中常用的按键别名：

@keyup.   @keydown. 
    回车=>enter
    删除=>delete（捕获“删除”和“退格"键）
    退出=>esc
    空格=>space
    换行=>tab 得用@keydown.绑定 因为tab本身作用切换焦点 导致当前元素没有焦点就触发不了keyup了
    上=>up
    下=>down
    左=>left
    右=>right
    event.key 如果是多个单词的 在绑定的大小写需要都是小写并且中间用@keyup. - 拼接名字
2.Vue未提供别名的按键，可以使用按键原始的key值去绑定，但注意要转为kebab-case（短横线命名）
3.系统修饰键（用法特殊）：ctrl、alt、shift、meta 可以连续使用@keydown.ctrl.y
    （1）.配合keyup使用：按下修饰键的同时，再按下其他键，随后释放其他键，事件才被触发。
    （2）.配合keydown使用：正常触发事件。
4.也可以使用keyCode去指定具体的按键（不推荐）
5.Vue.config.keyCodes.自定义键名=键码，可以去定制按键别名
