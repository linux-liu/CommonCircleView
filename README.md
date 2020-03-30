# CommonCircleView
一个带阴影效果的圆形控制的View,可以上下左右中进行控制
用法：
```xml
 <!--   app:center_text="@string/tv"  中间的文本
        app:center_icon="@drawable/ok_tv_normal" 中间的图标 两个都设置优先使用图标
        app:center_is_can_click="true"   中间是否可以点击 默认可以
        app:control_radius="85dp"  圆的半径 设置这个来控制view的大小，设置宽高没有用的
        app:first_icon="@drawable/up_tv" //第一个图标 图标顺序为顺时针 上 右 下 左
        app:four_icon="@drawable/left_tv" 第四个图标
        app:icon_num="4" //设置图标的个数不包括中间的
        app:second_icon="@drawable/right_tv" 第二个图标
        app:third_icon="@drawable/down_tv" 第三个图标
        -->

<com.at.smarthome.commoncircleview
        android:id="@+id/cv_tv"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:center_icon="@drawable/ok_tv_normal"
        app:center_is_can_click="true"
        app:control_radius="85dp"
        app:first_icon="@drawable/up_tv"
        app:four_icon="@drawable/left_tv"
        app:icon_num="3"
        app:second_icon="@drawable/right_tv"
        app:third_icon="@drawable/down_tv" />

```
本圆形控制View，支持中间小圆里可以放文字或者图标，圆边上的图标最多支持4个图标，可以通过xml自定义属性动态配置1-4个图标。还可以为其点击区域添加点击事件。非常灵活
具体详解介绍请访问https://blog.csdn.net/u014795729/article/details/105204708
如果觉得对你有用，给个star，(*^__^*) 
