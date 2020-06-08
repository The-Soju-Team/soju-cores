//--------- websocket ----------------------------------
/*(function() {
    var Sock = function() {
        var socket;
        if (!window.WebSocket) {
            window.WebSocket = window.MozWebSocket;
        }

        if (window.WebSocket) {
            socket = new WebSocket("ws://localhost:8070");
            socket.onopen = onopen;
            socket.onmessage = onmessage;
            socket.onclose = onclose;
        } else {
            alert("Your browser does not support Web Socket.");
        }

        function onopen(event) {
            console.log("Web Socket opened!");
        }

        function onmessage(event) {
            appendTextArea(event.data);
        }
        function onclose(event) {
            appendTextArea("Web Socket closed");
        }

        function appendTextArea(newData) {
            var el = getTextAreaElement();
            el.value = el.value + '\n' + newData;
        }

        function getTextAreaElement() {
            return document.getElementById('responseText');
        }

        function send(event) {
            event.preventDefault();
            if (window.WebSocket) {
                if (socket.readyState == WebSocket.OPEN) {
                    socket.send(event.target.message.value);
                } else {
                    alert("The socket is not open.");
                }
            }
			document.getElementById('message').value = '';
        }
        document.forms.inputform.addEventListener('submit', send, false);
    }
    window.addEventListener('load', function() { new Sock(); }, false);
})();*/

getMyMessage = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/getmymessage',
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            var divMessage = document.getElementById('G_Message');
            var innerDiv = '';
            for(var i = 0; i < data.message.length; i++) {
                innerDiv += '<a href="#" class="list-group-item">' +
                            '<p class="contacts-title pull-left">' + data.message[i].sender_name + '</p><p class="pull-right" style="color:#0571ce">' + data.message[i].send_date + '</p><br/>' +
                            '<img src="/viewimage?filename=' + data.message[i].sender_picture + '" style="width:60px; height:60px" class="pull-left" alt="' + data.message[i].sender_name + '"/>' +
                            '<p>' + data.message[i].content + '</p>' +
                        '</a>'
            }
            divMessage.innerHTML = innerDiv;
        }
    });    
}