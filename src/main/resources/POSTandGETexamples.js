function POST_addBook(){

    var Title = document.getElementById("title").value;
    var Price = document.getElementById("price").value;
    var Quantity = document.getElementById("quantity").value;
    var Url = document.getElementById("url").value;

    var data = {
        "title": Title,
        "price": Price,
        "quantity": Quantity,
        "Url": Url
    };


    // 通信実行
    $.ajax({
        type:"post",
        url:"http://localhost:8080/sample/addBook",
        data:JSON.stringify(data),
        contentType: 'application/json',
        dataType: "json",
        crossDomain: true,
        headers: {
            'Access-Control-Allow-Origin': '*'
        },
        success: function(json_data) {
            // 成功時の処理
            alert("Added!");
        }
    });

}