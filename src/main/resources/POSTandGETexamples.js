



function PATCH_updateBookStatus(){

    var bookid2 = document.getElementById("bookid2").value;
    var phone_number2 = document.getElementById("phone_number2").value;
    var bookstatus = document.getElementById("bookstatus").value;

    console.log(bookid2+","+phone_number2+","+bookstatus);

    var data = {
        "book_id": bookid2,
        "phone_number": phone_number2,
        "status": bookstatus
    };


    // 通信実行
    $.ajax({
        type:"PATCH",
        url: "https://takaakidemo.herokuapp.com/sample/updateBookStatus",
        //url: "http://localhost:8080/sample/updateBookStatus",
        data:JSON.stringify(data),
        contentType: 'application/json',
        dataType: "json",
        crossDomain: true,
        headers: {
            'Access-Control-Allow-Origin': '*'
        }
    }).then(
        // 1つめは通信成功時のコールバック
        function (data,status, xhr) {
            //alert(JSON.stringify(data,null,"\t"));
            document.getElementById("patchBookStatus").value = JSON.stringify(data,null,"\t");
            //alert(FormatJSON(data));
        },
        // 2つめは通信失敗時のコールバック
        function () {
            alert("読み込み失敗");
        });


}

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
        type:"PUT",
        url: "https://takaakidemo.herokuapp.com/sample/addBook",
        //url: "http://localhost:8080/sample/addBook",
        data:JSON.stringify(data),
        contentType: 'application/json',
        dataType: "json",
        crossDomain: true,
        headers: {
            'Access-Control-Allow-Origin': '*'
        }
    }).then(
        // 1つめは通信成功時のコールバック
        function (data,status, xhr) {
            //alert(JSON.stringify(data,null,"\t"));
            document.getElementById("ADDresult").value = JSON.stringify(data,null,"\t");
            //alert(FormatJSON(data));
        },
        // 2つめは通信失敗時のコールバック
        function () {
            alert("読み込み失敗");
        });
}




function GET_searchAllBooks(){
    // 通信実行
    $.ajax({
        url: 'https://takaakidemo.herokuapp.com/sample/getAllBooks',
        //url: "http://localhost:8080/sample/getAllBooks",
        type: 'GET',
        dataType: 'json',
        timeout: 5000,
        crossDomain: true,
        headers: {
            'Access-Control-Allow-Origin': '*'
        }
    }).then(
        function (data,status, xhr) {
            document.getElementById("getBooksresult").value = JSON.stringify(data,null,"\t");
        },
        function () {
            alert("読み込み失敗");
        });
}



function GET_searchBook(){

    var id = document.getElementById("bookid").value;
    //console.log(bid);
    /*var data = {
        "id": id
    };*/
    $.ajax({
        url: 'https://takaakidemo.herokuapp.com/sample/getBook?id='+id,
        //url: "http://localhost:8080/sample/getBook?id="+id,
        type: 'GET',
        //dataType: 'json',
        //data:JSON.stringify(data),
        timeout: 5000,
        crossDomain: true,
        headers: {
            'Access-Control-Allow-Origin': '*'
        }
    }).then(
        function (data,status, xhr) {
            document.getElementById("getBookresult").value = JSON.stringify(data,null,"\t");
        },
        function () {
            alert("読み込み失敗");
        });
}







function DELETE_removeBook(){

    var id = document.getElementById("bookid_delete").value;

    $.ajax({
        url: 'https://takaakidemo.herokuapp.com/sample/deleteBook?id='+id,
        //url: "http://localhost:8080/sample/deleteBook?id="+id,
        type: 'GET',
        timeout: 5000,
        crossDomain: true,
        headers: {
            'Access-Control-Allow-Origin': '*'
        }
    }).then(
        function (data,status, xhr) {
            console.log(xhr.statusCode());
            document.getElementById("deleteBookresult").value = JSON.stringify(data,null,"\t");
        },
        function (XMLHttpRequest, textStatus, errorThrown) {
            alert("読み込み失敗: XMLHttpRequest : " + XMLHttpRequest.status+", textStatus : " + textStatus+", errorThrown : " + errorThrown.message);
        });
}













