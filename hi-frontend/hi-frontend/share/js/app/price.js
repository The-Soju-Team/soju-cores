viewPrice = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/viewprice',
        data: {},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            if(data.day_1_1) document.getElementById('day_1_1').value = data.day_1_1;
            if(data.day_1_2) document.getElementById('day_1_2').value = data.day_1_2;
            if(data.day_1_3) document.getElementById('day_1_3').value = data.day_1_3;
            if(data.day_1_4) document.getElementById('day_1_4').value = data.day_1_4;
            if(data.day_1_5) document.getElementById('day_1_5').value = data.day_1_5;
            if(data.day_1_6) document.getElementById('day_1_6').value = data.day_1_6;
            if(data.day_1_7) document.getElementById('day_1_7').value = data.day_1_7;
            
            if(data.day_3_1) document.getElementById('day_3_1').value = data.day_3_1;
            if(data.day_3_2) document.getElementById('day_3_2').value = data.day_3_2;
            if(data.day_3_3) document.getElementById('day_3_3').value = data.day_3_3;
            if(data.day_3_4) document.getElementById('day_3_4').value = data.day_3_4;
            if(data.day_3_5) document.getElementById('day_3_5').value = data.day_3_5;
            if(data.day_3_6) document.getElementById('day_3_6').value = data.day_3_6;
            if(data.day_3_7) document.getElementById('day_3_7').value = data.day_3_7;
            
            if(data.day_6_1) document.getElementById('day_6_1').value = data.day_6_1;
            if(data.day_6_2) document.getElementById('day_6_2').value = data.day_6_2;
            if(data.day_6_3) document.getElementById('day_6_3').value = data.day_6_3;
            if(data.day_6_4) document.getElementById('day_6_4').value = data.day_6_4;
            if(data.day_6_5) document.getElementById('day_6_5').value = data.day_6_5;
            if(data.day_6_6) document.getElementById('day_6_6').value = data.day_6_6;
            if(data.day_6_7) document.getElementById('day_6_7').value = data.day_6_7;

            if(data.day_12_1) document.getElementById('day_12_1').value = data.day_12_1;
            if(data.day_12_2) document.getElementById('day_12_2').value = data.day_12_2;
            if(data.day_12_3) document.getElementById('day_12_3').value = data.day_12_3;
            if(data.day_12_4) document.getElementById('day_12_4').value = data.day_12_4;
            if(data.day_12_5) document.getElementById('day_12_5').value = data.day_12_5;
            if(data.day_12_6) document.getElementById('day_12_6').value = data.day_12_6;
            if(data.day_12_7) document.getElementById('day_12_7').value = data.day_12_7;

            if(data.night_1_1) document.getElementById('night_1_1').value = data.night_1_1;
            if(data.night_1_2) document.getElementById('night_1_2').value = data.night_1_2;
            if(data.night_1_3) document.getElementById('night_1_3').value = data.night_1_3;
            if(data.night_1_4) document.getElementById('night_1_4').value = data.night_1_4;
            if(data.night_1_5) document.getElementById('night_1_5').value = data.night_1_5;
            if(data.night_1_6) document.getElementById('night_1_6').value = data.night_1_6;
            if(data.night_1_7) document.getElementById('night_1_7').value = data.night_1_7;
            
            if(data.night_3_1) document.getElementById('night_3_1').value = data.night_3_1;
            if(data.night_3_2) document.getElementById('night_3_2').value = data.night_3_2;
            if(data.night_3_3) document.getElementById('night_3_3').value = data.night_3_3;
            if(data.night_3_4) document.getElementById('night_3_4').value = data.night_3_4;
            if(data.night_3_5) document.getElementById('night_3_5').value = data.night_3_5;
            if(data.night_3_6) document.getElementById('night_3_6').value = data.night_3_6;
            if(data.night_1_7) document.getElementById('night_3_7').value = data.night_3_7;
            
            if(data.night_6_1) document.getElementById('night_6_1').value = data.night_6_1;
            if(data.night_6_2) document.getElementById('night_6_2').value = data.night_6_2;
            if(data.night_6_3) document.getElementById('night_6_3').value = data.night_6_3;
            if(data.night_6_4) document.getElementById('night_6_4').value = data.night_6_4;
            if(data.night_6_5) document.getElementById('night_6_5').value = data.night_6_5;
            if(data.night_6_6) document.getElementById('night_6_6').value = data.night_6_6;
            if(data.night_6_7) document.getElementById('night_6_7').value = data.night_6_7;

            if(data.night_12_1) document.getElementById('night_12_1').value = data.night_12_1;
            if(data.night_12_2) document.getElementById('night_12_2').value = data.night_12_2;
            if(data.night_12_3) document.getElementById('night_12_3').value = data.night_12_3;
            if(data.night_12_4) document.getElementById('night_12_4').value = data.night_12_4;
            if(data.night_12_5) document.getElementById('night_12_5').value = data.night_12_5;
            if(data.night_12_6) document.getElementById('night_12_6').value = data.night_12_6;
            if(data.night_12_7) document.getElementById('night_12_7').value = data.night_12_7;
        }
    });
}

updatePrice = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/updateprice',
        data: $("#updateForm").serialize(),
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            $.noty.closeAll();
            successMessage('Ghi lại thành công!');
        }
    });
}

successMessage = function(text) {
    noty({
        text: text,
        layout: 'center',
        type: 'success'
    });  
    setTimeout(function(){ $.noty.closeAll(); }, 2000);
}