HTML <input> 元素的 type 属性支持多种输入类型，不同类型对应不同的表单控件和交互方式，以下是主要类型及其用途：

一、基础输入类型
‌文本输入‌

type="text"：单行文本输入框，常用于用户名、搜索框等短文本场景4
type="password"：密码输入框，输入内容以掩码（圆点或星号）显示4
type="email"：邮箱输入框，自动验证输入是否符合邮箱格式规则4
‌选择控件‌

type="radio"：单选按钮，需为同一组选项设置相同name属性以实现互斥选择4
type="checkbox"：复选框，支持多选，可通过checked属性设置默认选中状态4
‌按钮类‌

type="submit"：提交按钮，用于触发表单数据提交到服务器4
type="reset"：重置按钮，清空表单内已输入内容并恢复初始值4
type="button"：普通按钮，常配合JavaScript实现自定义交互逻辑4
‌文件与图像‌

type="file"：文件上传控件，允许用户选择本地文件上传4
type="image"：图像式提交按钮，通过src属性指定图片路径替代默认按钮样式4
二、特殊用途类型
‌隐藏域‌

type="hidden"：隐藏输入字段，用于存储无需用户查看或修改的后台数据4
‌数值与日期‌

type="number"：数字输入框，支持设置最小值（min）和最大值（max）5
type="date"：日期选择器，提供可视化日历控件选择日期（需浏览器支持）5
‌颜色选择‌

type="color"：颜色选择器，允许用户通过调色板选择颜色值5
三、HTML5新增类型
‌验证增强类型‌
type="tel"：电话号码输入框，移动端可能唤起数字键盘5
type="url"：URL输入框，验证输入是否为有效网址格式5
‌说明‌：部分类型（如date、color）的显示效果和功能依赖浏览器支持，实际开发中需做好兼容