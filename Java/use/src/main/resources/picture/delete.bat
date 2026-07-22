@echo off
setlocal enabledelayedexpansion

:: 设置目标目录为当前目录
set target_dir=.

:: 遍历目标目录下的所有文件夹及子文件夹中的文件
for /r "%target_dir%" %%f in (*) do (
    :: 获取文件扩展名
    set "ext=%%~xf"
    
    :: 检查扩展名是否为 .png 或 .jpg
    if /i not "!ext!"==".png" if /i not "!ext!"==".jpg" if /i not "!ext!"==".bat" (
        echo 删除文件: %%f
        del "%%f" >nul 2>&1
    )
)

echo 删除完成。
pause
