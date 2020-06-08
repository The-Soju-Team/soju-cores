searchOrder = function() {
    $('#fromdate').datepicker({pickTime: false, format: "dd-mm-yyyy" });
    $('#todate').datepicker({pickTime: false, format: "dd-mm-yyyy" });     
    $('#orderTable').DataTable( {
        destroy: true,
        processing: false,
        serverSide: true,
        searching: false,    
        bSort: false,
        columnDefs: [ 
            {
                targets: 4,
                render: function (data, type, full, meta ) {
                    if(data) {
                        var dataValue = data.replace(/,/g , '<br/>');
                        return dataValue;
                    } else return data;
                }
            },
            {
                targets: 5,
                render: function (data, type, full, meta ) {
                    if(data) {
                        if(full[8] == 1) {
                            if(data == '1') return '1 ngày';
                            else if(data == '2') return '1 tháng';
                            else if(data == '3') return '1 quý';
                            else return data;
                        } else {
                            var dataValue = data.replace(/,/g , ' phút<br/>');
                            dataValue += ' phút';
                            return dataValue;
                        }
                    } else return data;
                }
            }            
        ],
        ajax: {
            type: 'POST',
            dataType: 'json',
            url: '/searchorder',
            data: function ( d ) {
                d.ordercode = $('#ordercode').val();
                d.customer = $('#customer').val();
                d.mobile = $('#mobile').val();
                d.status = $('#status').val();      
                d.fromdate = $('#fromdate').val();
                d.todate = $('#todate').val();
            },
            cache: false
        },
        language: {
            'decimal':        '',
            'emptyTable':     'Không có dữ liệu',
            'info':           'Từ _START_ đến _END_ của _TOTAL_ bản ghi',
            'infoEmpty':      'Từ 0 đến 0 của 0 bản ghi',
            'infoFiltered':   '(Lọc từ _MAX_ tổng số bản ghi)',
            'infoPostFix':    '',
            'thousands':      ',',
            'lengthMenu':     'Hiển thị _MENU_ dòng/trang',
            'loadingRecords': 'Đang tải...',
            'processing':     'Đang xử lý...',
            'search':         'Tìm kiếm:',
            'zeroRecords':    'Không tìm thấy dữ liệu',
            'paginate': {
                'first':      'Đầu',
                'last':       'Cuối',
                'next':       'Tiếp',
                'previous':   'Trước'
            },
            'aria': {
                'sortAscending':  ': Sắp xếp tăng dần',
                'sortDescending': ': Sắp xếp giảm dần'
            }
        }
    } );
}

errorMessage = function(text) {
    noty({
        text: text,
        layout: 'center',
        type: 'info',
        buttons: [
                {addClass: 'btn btn-info btn-clean', text: 'Đóng', onClick: function($noty) {
                    $noty.close();
                }}
            ]
    });    
}

successMessage = function(text) {
    noty({
        text: text,
        layout: 'center',
        type: 'success'
    });  
    setTimeout(function(){ closeAllMessage(); }, 2000);
}

backToList = function() {
    var parentPage = '';
    if(document.getElementById('parent')) parentPage = document.getElementById('parent').value;
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/backlistuser',
        data: {parent: parentPage},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            $('#searchForm').keypress(function (e) {
                if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
                    searchOrder();
                    return false;
                } else {
                    return true;
                }
            });
            
            searchOrder();
            document.getElementById('ordercode').focus();
        }
    }); 
}

viewOrder = function(id) {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/vieworder',
        data: {orderid: id},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            var parentPage = document.getElementById('parent').value;
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            document.getElementById('parent').value = parentPage;  
            configDropzoneFile();
            document.getElementById('orderId').value = data.order.order_id;  
            if(data.order.is_periodical == 1) {
                document.getElementById('periodicalDiv').style.display = '';
                document.getElementById('rowStart').style.display = 'none';
                if(data.order.start_date) document.getElementById('lblfrom').innerHTML = data.order.start_date;
                if(data.order.start_length) {
                    if(data.order.start_length == '1') document.getElementById('lblexpire').innerHTML = '1 tuần';
                    if(data.order.start_length == '2') document.getElementById('lblexpire').innerHTML = '1 tháng';
                    if(data.order.start_length == '3') document.getElementById('lblexpire').innerHTML = '1 quý';
                }
            } else {
                if(data.order.start_date && data.order.start_length) {
                    var innerStart = data.order.start_date.split(',');
                    var innerLength = data.order.start_length.split(',');
                    var innerFinal = '';
                    for(var i = 0; i < innerStart.length; i++) {
                        innerFinal += innerStart[i] + ' (' + innerLength[i] + ' phút)' + '<br/>';
                    }
                    document.getElementById('lblstart').innerHTML = innerFinal;
                    document.getElementById('periodicalDiv').style.display = 'none';
                    document.getElementById('rowStart').style.display = '';
                }
            }
            if(data.order.order_code) document.getElementById('lblordercode').innerHTML = data.order.order_code;
            if(data.order.customer_name) document.getElementById('lblcustomer').innerHTML = data.order.customer_name;
            if(data.order.price) document.getElementById('lblprice').innerHTML = separate(data.order.price.toString());
            if(data.order.status) document.getElementById('lblstatus').innerHTML = data.order.status;
            if(data.order.detail) {
                if(document.getElementById('lbladdress')) document.getElementById('lbladdress').innerHTML = data.order.detail;
                if(document.getElementById('latitude')) document.getElementById('latitude').value = data.order.latitude;
                if(document.getElementById('longitude')) document.getElementById('longitude').value = data.order.longitude;
                if(document.getElementById('latitude') && document.getElementById('longitude')) {
                    loadScript('https://maps.googleapis.com/maps/api/js?key=AIzaSyD18jCVXG3nu0ki_CCxmXYBszepbOZSYok&callback=initMap');
                }
            }
            
            if(data.order.content && data.order.content != null) document.getElementById('lblcontent').innerHTML = data.order.content;
            if(data.order.create_date) document.getElementById('lblcreate').innerHTML = data.order.create_date;
            if(data.order.cleaning == '1') document.getElementById('cleaning').checked = true;
            else document.getElementById('cleaning').checked = false;
            if(data.order.cooking == '1') document.getElementById('cooking').checked = true;
            else document.getElementById('cooking').checked = false;
            if(data.order.babycare == '1') document.getElementById('babycare').checked = true;
            else document.getElementById('babycare').checked = false;
            if(data.order.oldcare == '1') document.getElementById('oldcare').checked = true;
            else document.getElementById('oldcare').checked = false;
            if(data.order.mon_start) document.getElementById('monstart').innerHTML = data.order.mon_start.replace(/,/g , '<br/>');
            if(data.order.mon_end) document.getElementById('monend').innerHTML = data.order.mon_end.replace(/,/g , ' phút<br/>') + ' phút';
            if(data.order.tue_start) document.getElementById('tuestart').innerHTML = data.order.tue_start.replace(/,/g , '<br/>');
            if(data.order.tue_end) document.getElementById('tueend').innerHTML = data.order.tue_end.replace(/,/g , ' phút<br/>') + ' phút';
            if(data.order.wed_start) document.getElementById('wedstart').innerHTML = data.order.wed_start.replace(/,/g , '<br/>');
            if(data.order.wed_end) document.getElementById('wedend').innerHTML = data.order.wed_end.replace(/,/g , ' phút<br/>') + ' phút';
            if(data.order.thu_start) document.getElementById('thustart').innerHTML = data.order.thu_start.replace(/,/g , '<br/>');
            if(data.order.thu_end) document.getElementById('thuend').innerHTML = data.order.thu_end.replace(/,/g , ' phút<br/>') + ' phút';
            if(data.order.fri_start) document.getElementById('fristart').innerHTML = data.order.fri_start.replace(/,/g , '<br/>');
            if(data.order.fri_end) document.getElementById('friend').innerHTML = data.order.fri_end.replace(/,/g , ' phút<br/>') + ' phút';
            if(data.order.sat_start) document.getElementById('satstart').innerHTML = data.order.sat_start.replace(/,/g , '<br/>');
            if(data.order.sat_end) document.getElementById('satend').innerHTML = data.order.sat_end.replace(/,/g , ' phút<br/>') + ' phút';
            if(data.order.sun_start) document.getElementById('sunstart').innerHTML = data.order.sun_start.replace(/,/g , '<br/>');
            if(data.order.sun_end) document.getElementById('sunend').innerHTML = data.order.sun_end.replace(/,/g , ' phút<br/>') + ' phút';
            if(data.order.customer_picture) {
                document.getElementById('customerView').innerHTML = '<img src="/image?filename=' + data.order.customer_picture + '" style="height:150px; border-radius: 3px;"/>';
            }
            if(data.order.order_file) {
                document.getElementById('fileView').innerHTML = '<a href="/orderfile?filename=' + data.order.order_file + '" class="btn btn-info"><span class="fa fa-download"></span> Tải xuống </button>';
            }
        }
    });    
}

function loadScript(src,callback){
    var script = document.createElement("script");
    script.type = "text/javascript";
    if(callback)script.onload=callback;
    document.getElementsByTagName("head")[0].appendChild(script);
    script.src = src;
}

function initMap() {
    var uluru = {lat: parseFloat(document.getElementById('latitude').value), lng: parseFloat(document.getElementById('longitude').value)};
    var map = new google.maps.Map(document.getElementById('map'), {
        zoom: 16,
        center: uluru
    });
    var marker = new google.maps.Marker({
        position: uluru,
        map: map
    });
}

separate = function(text) {
    var count = 0;
    for(var i = text.length - 1 ; i >= 0; i--) {
        count++;
        if(i != 0 && count % 3 === 0) {
            text = text.slice(0, i) + ',' + text.slice(i);
        }
    }
    return text;
}

var myFilezone;
configDropzoneFile = function() {
    Dropzone.autoDiscover = false;
    myFilezone = new Dropzone('#fileForm',{ 
        url: '/uploadorderfile', 
        method: 'post',
        paramName: 'file', // The name that will be used to transfer the file
        maxFilesize: 5, // MB
        maxFiles: 1,
        dictMaxFilesExceeded: 'Bạn hãy chọn tối đa 1 ảnh!',
        dictFileTooBig: 'Kích thước file tối đa chỉ 500KB!',
        init: function() {
            this.on('error', function(file, errorFile, third) {
                if (errorFile === 'Bạn hãy chọn tối đa 1 file!') {
                    document.getElementById('errorFile').innerHTML = 'Bạn hãy chọn tối đa 1 ảnh!';
                    document.getElementById('errorFile').style.display = '';
                    this.removeFile(file);
                }
                if (errorFile === 'Kích thước file tối đa chỉ 5MB!') {
                    document.getElementById('errorFile').innerHTML = 'Kích thước file tối đa chỉ 500KB!';
                    document.getElementById('errorFile').style.display = '';
                    this.removeFile(file);
                }
            });
        },                                
        accept: function(file, done) {
            document.getElementById('errorFile').innerHTML = '';
            document.getElementById('errorFile').style.display = 'none';
            
            if (this.files.length) {
                var _i, _len;
                for (_i = 0, _len = this.files.length; _i < _len - 1; _i++) // -1 to exclude current file
                {
                    if(this.files[_i].name === file.name && this.files[_i].size === file.size && this.files[_i].lastModifiedDate.toString() === file.lastModifiedDate.toString())
                    {
                        document.getElementById('errorFile').innerHTML = 'File này đã được tải lên server rồi!';
                        document.getElementById('errorFile').style.display = '';                        
                        this.removeFile(file);
                    }
                }
            }
            done();
        },
        success: function(file, response){
            var data = JSON.parse(response);
            setRemoveFunction(data.localFileName,data.serverFileName);
            document.getElementById('fileView').innerHTML = '<a href="/orderfile?filename=' + data.serverFileName + '" class="btn btn-info"><span class="fa fa-download"></span> Tải xuống </button>';
        }
    });    
}