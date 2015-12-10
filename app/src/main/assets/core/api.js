/**
 * 定义与Js调用Native相关的事件监听
 */
 function onToast(){
    var text = window.jsInterface.onToast("从JS中传递过来的文本！！！");
    alert(text);
 }