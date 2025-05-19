# note

all note for vue2 ........

# vue2 code 中有个疑问
# 三重 ```后面跟语言完整记录语言
```html
<input type="checkbox" id="single" v-model="singleValue">
<label>{{singleValue}}</label><br><br>

如果 singleValue没有初始值 选择框旁边就没有 singleValue
如果 singleValue初始值赋值false 选择框任然是选中状态
感觉必须赋值true 然后复选框选中状态
