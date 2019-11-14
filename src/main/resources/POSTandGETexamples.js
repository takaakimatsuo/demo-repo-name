function POST_addBook(){

    var Title = document.getElementById("title").value;
    var Price = document.getElementById("price").value;
    var Quantity = document.getElementById("quantity").value;
    var Url = document.getElementById("url").value;

    var data = {
        "title": Title,
        "price": Price,
        "quantity": Quantity,
        "url": Url
    };


    // 通信実行
    $.ajax({
        type:"POST",
        //url: "https://takaakidemo.herokuapp.com/sample/addBook",
        url: "http://localhost:8080/sample/addBook",
        data:JSON.stringify(data),
        contentType: 'application/json',
        dataType: "json",
        crossDomain: true,
        headers: {
            'Access-Control-Allow-Origin': '*'
        }
    }).then(
        // 1つめは通信成功時のコールバック
        function (data) {
            //alert(JSON.stringify(data,null,"\t"));
            document.getElementById("ADDresult").value = JSON.stringify(data,null,"\t");
            //alert(FormatJSON(data));
        },
        // 2つめは通信失敗時のコールバック
        function () {
            alert("読み込み失敗");
        });
}




function GET_getAllBooks(){
    // 通信実行
    $.ajax({
        //url: 'https://takaakidemo.herokuapp.com/sample/getAllBooks',
        url: "http://localhost:8080/sample/getAllBooks",
        type: 'GET',
        dataType: 'json',
        timeout: 5000,
        crossDomain: true,
        headers: {
            'Access-Control-Allow-Origin': '*'
        }
    }).then(
        function (data) {
            document.getElementById("getBooksresult").value = JSON.stringify(data,null,"\t");
        },
        function () {
            alert("読み込み失敗");
        });
}