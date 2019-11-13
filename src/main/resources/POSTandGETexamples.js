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
        url: "https://takaakidemo.herokuapp.com/sample/addBook",
        data:JSON.stringify(data),
        contentType: 'application/json',
        dataType: "json",
        crossDomain: true,
        headers: {
            'Access-Control-Allow-Origin': '*'
        },
        success: function (data) {
            alert(JSON.stringify(data));
            console.log(data);
        },success: function () {
            alert("Success, but with no response.");
        },
        error: function(xhr, status, error){
            alert("Cannot get data:"+xhr.responseText);
            console.log(xhr.responseText);
        }
    });
}




function GET_getAllBooks(){
    // 通信実行
    $.ajax({
        url: 'https://takaakidemo.herokuapp.com/sample/getAllBooks',
        type: 'GET',
        dataType: 'json',
        timeout: 5000,
        crossDomain: true,
        headers: {
            'Access-Control-Allow-Origin': '*'
        }
    })
        .done(function(data) {
            // 通信成功時の処理を記述
            alert("done");
        })
        .fail(function(jqax) {
            // 通信失敗時の処理を記述
            alert("Failed");
            console.log(jqax);
        });
}