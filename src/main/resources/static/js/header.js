function beforeLoad(){
    let accessToken = localStorage.getItem('accessToken');
    let refreshToken = localStorage.getItem('refreshToken');

    let xhr = new XMLHttpRequest();
    xhr.open('GET','/**');
    xhr.setRequestHeader('accessToken',accessToken);
    xhr.setRequestHeader('refreshToken',refreshToken);
    xhr.send();
}

function login() {
    let username = $('input[name=username]')[0];
    let password = $('input[name=password][type=password]')[0];
    $.ajax({
        url: '/login',
        method: 'post',
        contentType: 'application/json',
        dataType: "JSON",
        data: JSON.stringify({
            username: username.value,
            password: password.value
        }),
        statusCode: {
            201: function (data) {
                console.log("로그인 성공")
                const accessToken = data.getResponseHeader('accessToken');
                const refreshToken = data.getResponseHeader('refreshToken');
                localStorage.setItem('accessToken', accessToken);
                localStorage.setItem('refreshToken', refreshToken);
            },
            200: function (result) {
                console.log("로그인 실패")
                alert("result: " + result.message);
            },
            401: function (textStatus, errorThrown) {
                console.log("Request failed:", textStatus, errorThrown);
                alert(textStatus);
            }
        }
    })
}