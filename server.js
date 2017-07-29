var http = require('http');
var mysql = require('mysql');
var url = require('url');
var querystring = require('querystring');
var conInfo = {
	host : '127.0.0.1', // 데이터베이스 아이피 또는 url
	user : 'root',      // 사용자 아이디
	password : 'mysql', // 비밀번호
	port : '3306',        // 포트
	database : 'mydb'    // 데이터베이스
};

var server = http.createServer((request, response)=>{
    
    // 1. 요청 url 분석 처리
    // /airbnb/search?checkin=20170727&checkout=20170731        -- 하나의 속성일 경우 querystring
    // /airbnb/search/20170728          -- 하나의 객체일 경우 RESTful 로, house 에 관련한 데이터를 요청하고 있다.
    // object 최소 단위가 레코드 최소단위이기 때문에 house는 object이고, 거기서 레코드를 검색한다면 여기까지는 Restful로 /airbnb/house/3 3번집 검색까지
    // 여기서 구체적인 속성을 검색할 때 /airbnb/house?guestS>3 처럼 한다. 즉, house object 의 멤버 변수에 접근할 때 사용한다고 생각하자.
    // /airbnb/search?checkin=20170727&checkout=20170731
    if(request.url.toLowerCase().startsWith('/airbnb/house')){
        // 가.검색 조건이 있는 검색
        // executeQuery(response);
        // 나.검색 조건이 있는 검색
        // var search = {
        //     checkin : "2017-07-27",
        //     checkout : "2017-07-31"
        // }

        // 쿼리스트링 부분을 뗴어내 준다
        var parsedUrl = url.parse(request.url);
        // 이걸 해주면 url 이 쿼리스트링이 되는군
        var search = querystring.parse(parsedUrl.query);
        console.log(search);
        
        // querystring 이 유동적으로 들어오기 때문에 하드코딩하면 적절히 대처할 수 없다
        executeQuery(response, search);
    } else {
        response.writeHead(404, {'Content-Type':'text/html'});
        response.end('<h1>404 Page Not Found</h1>');
    }
});

server.listen(80, () => console.log('server is running') );

function executeQuery(response, search){

    /** 여기는 공부하기 위한 것으로 반드시 마스터 해야 한다.
    var query = 'select * from house a join reservation b '
                + 'on a.id=b.house_id ';
    var values = [];
    if(search){
        // var keys = Object.keys(search);
        // console.log("keys ======"+keys);
        query = query + ' where 1=1';
        // for(var i=0; i<keys.length; i++){
        //     query += ' and '+keys[i]+'=?';
        // }
        // ****** 객체로 넘어오는 search 에서 key 와 value 를 꺼내는 방법 ******
        for(var key in search){ // saerch 는 확실히 객체이지만 키 값을 꺼낼 때 이렇게 배열처럼 꺼내주는군. key 가 아니어도 되는 것을 보면 반드시 키 값만 반환됨을 알 수 있다.
                                // 그렇다는 것은 jsonstring 객체에서 뭔가 값을 꺼내면 그것이 반드시 키값이라는 것이고, 키를 통해서 값을 꺼낼 수 있는 것이었다.
            // key 이름을 쿼리에 삽입하고
            query += ' and '+key+'=?';
            // key 로 조회한 값을 values 에 담는다
            values.push(search[key]);
        }
    }
    console.log(values);
    console.log(query);
    */

    var query = 'select * from house'
    if(search){
        query =  'select * from house where id not in '
                + ' (select house.id FROM house join reservation on house.id = reservation.house_id'
                + ' where '
                + ' ('+search.checkin+' <= reservation.checkin AND '+search.checkout+' >= reservation.checkin) or'
                + ' ('+search.checkout+' <= reservation.checkout AND '+search.checkout+' >= reservation.checkout) or'
                + ' (reservation.checkin <= '+search.checkin+' AND reservation.checkout >='+search.checkin+')'
                + ' )';
        if(search.guests > -1){
            query += ' and guests > '+ search.guests;
        };
        if(search.type > -1){
            query += ' and type = ' + search.type;
        };
        if(search.price > -1){
            query += ' and price = ' + search.price;
        };
        if(search.amenity > -1){
            query += ' and amenity = ' + search.amenity;
        };
    }
    console.log(query);

    var con = mysql.createConnection(conInfo);
    con.connect();
    con.query(query, function(err, items, fields){ // 데이터베이스에 쿼리 실행
        if(err){
            console.log(err);
        }else{
            sendResult(response, items);      
        }
        this.end();  // mysql 연결 해제
    });
}

function sendResult(response, items){
    var jsonString = JSON.stringify(items);
    console.log(jsonString);
    response.writeHead(200, {'Content-Type':'text/html;charset=utf-8'});
    response.end(jsonString);
}