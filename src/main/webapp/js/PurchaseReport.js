
layui.use('form', function(){
  var form = layui.form;

 //表单取值
  layui.$('#LAY-component-form-getval').on('click', function(){
    var data = form.val('example');
     var index = layer.load(1);
    $.ajax({
           //请求方式
           type : "POST",
           //请求的媒体类型
           contentType: "application/json;charset=UTF-8",
           //请求地址
           url : "/createPurchaseExcel",
           //数据，json字符串
           data : JSON.stringify(data),
           //请求成功
           success : function(result) {
                layer.close(index);
                layer.msg("进货额已经汇总完成");
           },
           //请求失败，包含具体的错误信息
           error : function(e){
               layer.close(index);
               console.log(e.status);
               console.log(e.responseText);
           }
       });
  });

});