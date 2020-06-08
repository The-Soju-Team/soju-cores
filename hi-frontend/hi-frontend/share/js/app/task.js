searchTask = function() {
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
                targets: 5,
                render: function (data, type, full, meta) {
                    if (data) {
                        var color = '#0571ce';
                        var time = '';
                        if(full[9] && full[8] && full[9] - full[8] > 900) {
                            time = ' Muộn giờ';
                            color = 'red';
                        }
                        var location = '';
                        if(full[12] && full[12] > 1) {
                            location = '<br/>Sai vị trí';
                            color = 'red';
                        }
                        return '<a "javascript:void(0)" style="color:' + color + '" data-toggle="modal" data-target="#selectModal" onclick="viewLocation(' + full[14]  + ', ' + full[15] + ', ' + full[18]  + ', ' + full[19]  + ', ' + full[20] + ', ' + full[21] + ', 1);">' + data + time + location + '</a>';
                    }
                    return data;
                }
            }, 
            {
                targets: 6,
                render: function (data, type, full, meta) {
                    if (data) {
                        var color = '#0571ce';
                        var time = '';
                        if(full[10] && full[11] && full[10] - full[11] > 1800) {
                            time = ' Thiếu giờ';
                            color = 'red';
                        }
                        var location = '';
                        if(full[13] && full[13] > 1) {
                            location = '<br/>Sai vị trí';
                            color = 'red';
                        }
                        return '<a "javascript:void(0)" style="color:' + color + '" data-toggle="modal" data-target="#selectModal" onclick="viewLocation(' + full[16]  + ', ' + full[17] + ', ' + full[18]  + ', ' + full[19]  + ', ' + full[20] + ', ' + full[21] + ', 0);">' + data + time + location + '</a>';
                    }
                    return data;
                }
            }            
        ],
        ajax: {
            type: 'POST',
            dataType: 'json',
            url: '/searchtask',
            data: function ( d ) {
                d.ordercode = $('#ordercode').val();
                d.maid = $('#maid').val();
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
                    searchTask();
                    return false;
                } else {
                    return true;
                }
            });
            var today = new Date();
            var dd = today.getDate();
            var mm = today.getMonth()+1; //January is 0!
            var yyyy = today.getFullYear();

            if(dd<10) {
                dd = '0'+dd
            } 

            if(mm<10) {
                mm = '0'+mm
            } 

            today = dd + '-' + mm + '-' + yyyy;

            document.getElementById('todate').value = today;
            searchTask();
            document.getElementById('ordercode').focus();
        }
    }); 
}

viewTask = function(id) {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/viewtask',
        data: {planid: id},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            var parentPage = document.getElementById('parent').value;
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            document.getElementById('parent').value = parentPage;
            document.getElementById('planId').value = data.plan.plan_id;
            if(data.plan.plan_code) document.getElementById('lblPlanCode').innerHTML = data.plan.plan_code;
            if(data.plan.status) {
                if(data.plan.status == 1) document.getElementById('lblStatus').innerHTML = 'Chưa thực hiện';
                if(data.plan.status == 2) document.getElementById('lblStatus').innerHTML = 'Đang thực hiện';
                if(data.plan.status == 3) document.getElementById('lblStatus').innerHTML = 'Hoàn thành';
                if(data.plan.status == 4) document.getElementById('lblStatus').innerHTML = 'Thanh toán';
                if(data.plan.status == 5) document.getElementById('lblStatus').innerHTML = 'Hủy';
                if(data.plan.status == 6) document.getElementById('lblStatus').innerHTML = 'Hết hiệu lực';
            }
            if(data.plan.customer_name) document.getElementById('lblCustomerName').innerHTML = data.plan.customer_name;
            
            if(data.plan.maid_name) document.getElementById('lblMaidName').innerHTML = data.plan.maid_name;
           
            if(data.plan.start_date) document.getElementById('lblStart').innerHTML = data.plan.start_date;
            if(data.plan.end_date) document.getElementById('lblEnd').innerHTML = data.plan.end_date;
            if(data.plan.real_start) document.getElementById('lblRealStart').innerHTML = data.plan.real_start;
            if(data.plan.real_end) document.getElementById('lblRealEnd').innerHTML = data.plan.real_end;
            if(data.plan.customer_rating) {
                var rating = data.plan.customer_rating;
                var innerTemp = '';
                for(var i = 0; i < rating; i++) innerTemp += '<span class="fa fa-star"></span>';
                for(var i = rating; i < 5; i++) innerTemp += '<span class="fa fa-star-o"></span>';
                document.getElementById('customerRatingDiv').innerHTML = innerTemp;
            }
            if(data.plan.time_comment) {
                document.getElementById('lblTimeComment').innerHTML = data.plan.time_comment;
            }
            if(data.plan.customer_comment) {
                document.getElementById('lblCustomerComment').innerHTML = data.plan.customer_comment;
            }
            if(data.plan.price) {
                document.getElementById('lblPrice').innerHTML = data.plan.price;
            }            
            
            if(data.plan.rating_customer) {
                var rating = data.plan.rating_customer;
                var innerTemp = '';
                for(var i = 0; i < rating; i++) innerTemp += '<span class="fa fa-star"></span>';
                for(var i = rating; i < 5; i++) innerTemp += '<span class="fa fa-star-o"></span>';
                document.getElementById('ratingCustomerDiv').innerHTML = innerTemp;
            }
            if(data.plan.rating_work) {
                document.getElementById('ratingWorkDiv').innerHTML = '<span class="fa fa-star"></span><span class="fa fa-star"></span><span class="fa fa-star"></span><span class="fa fa-star"></span><span class="fa fa-star"></span>';
            }            
            if(data.plan.time_maid_comment) {
                document.getElementById('lblTimeMaidComment').innerHTML = data.plan.time_maid_comment;
            }            
            if(data.plan.maid_comment) {
                document.getElementById('lblMaidComment').innerHTML = data.plan.maid_comment;
            }
            if(data.plan.salary) {
                document.getElementById('lblPrice').innerHTML = data.plan.salary;
            }

            if(data.plan.customer_picture) {
                document.getElementById('customerView').innerHTML = '<img src="/image?filename=' + data.plan.customer_picture + '&time=' + (new Date()).getTime() + '" style="width:100%; border-radius: 3px;"/>';
            }
            
            if(data.plan.maid_picture) {
                setTimeout(function () {
                        document.getElementById('maidView').innerHTML = '<img src="/image?filename=' + data.plan.maid_picture + '&time=' + (new Date()).getTime() + '" style="width:100%; border-radius: 3px;"/>';
                    }, 100);
            }            
            
            $('#realStart').datetimepicker({pickTime: true, format: "dd-mm-yyyy hh:ii" });
            $('#realEnd').datetimepicker({pickTime: true, format: "dd-mm-yyyy hh:ii" });
        }
    });    
}

editTask = function() {
    var obj = document.getElementById('editButton');
    var divStart = document.getElementById('realStart').parentNode;    
    var divEnd = document.getElementById('realEnd').parentNode;
    if(obj.innerHTML == '<span class="fa fa-pencil"></span> Sửa') {
        document.getElementById('lblRealStart').style.display = 'none';
        document.getElementById('lblRealEnd').style.display = 'none';
        obj.innerHTML = '<span class="fa fa-close"></span> Hủy bỏ';
        document.getElementById('saveButton').style.display = '';

        divStart.style.display = '';
        document.getElementById('realStart').value = document.getElementById('lblRealStart').innerHTML;
        
        divEnd.style.display = '';
        document.getElementById('realEnd').value = document.getElementById('lblRealEnd').innerHTML;
        
        document.getElementById('realStart').focus();
    } else {
        document.getElementById('lblRealStart').style.display = '';
        document.getElementById('lblRealEnd').style.display = '';
        obj.innerHTML = '<span class="fa fa-pencil"></span> Sửa';
        
        document.getElementById('saveButton').style.display = 'none';        
        divStart.style.display = 'none';
        divEnd.style.display = 'none';
    }
}

updateTask = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/updatetask',
        data: $("#updateForm").serialize(),
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            closeAllMessage();
            document.getElementById('lblRealStart').innerHTML = document.getElementById('realStart').value;
            document.getElementById('lblRealEnd').innerHTML = document.getElementById('realEnd').value;
            editTask();
            successMessage('Ghi lại thành công!');
        }
    });         
}

var taskFirstMap = 0;

viewLocation = function(realLat, realLong, lat, long , addressId, planId, isStart) {
    document.getElementById('lat').value = lat;
    document.getElementById('long').value = long;
    document.getElementById('realLat').value = realLat;
    document.getElementById('realLong').value = realLong;
    document.getElementById('addressId').value =  addressId;
    document.getElementById('planId').value =  planId;
    document.getElementById('isStart').value = isStart;
    if(taskFirstMap == 0) {
        taskFirstMap = 1;
        loadScript('https://maps.googleapis.com/maps/api/js?key=AIzaSyD18jCVXG3nu0ki_CCxmXYBszepbOZSYok&callback=initMapTask');
    } else {
        var locate = {lat: parseFloat(document.getElementById('lat').value), lng: parseFloat(document.getElementById('long').value)};
        var realLocate = {lat: parseFloat(document.getElementById('realLat').value), lng: parseFloat(document.getElementById('realLong').value)};
        taskMap.setCenter(locate);
        taskMaker.setPosition(locate);
        taskReal.setPosition(realLocate);
    }
}

var taskMap;
var taskMaker;
var taskReal;
function initMapTask() {
    var locate = {lat: parseFloat(document.getElementById('lat').value), lng: parseFloat(document.getElementById('long').value)};
    var realLocate = {lat: parseFloat(document.getElementById('realLat').value), lng: parseFloat(document.getElementById('realLong').value)};

    //---------------------------------------------------------------
    taskMap = new google.maps.Map(document.getElementById('map'), {
        center: locate,
        zoom: 14
    });

    //---------------------------------------------------------------
    taskMaker = new google.maps.Marker({
        position: locate,
        map: taskMap,        
        label: {
          color: '#222222',
          fontWeight: 'bold',
          text: 'Theo lịch'          
        }
    });

    taskReal = new google.maps.Marker({
        position: realLocate,
        map: taskMap,
        label: {
          color: '#222222',
          fontWeight: 'bold',
          text: 'Thực tế'
        }
    });
}

updateLocationOnClick = function() {
    noty({
        text: 'Bạn có muốn sửa ví trí trong lịch theo thực tế không?',
        layout: 'center',
        type: 'info',
        buttons: [
                {addClass: 'btn btn-info btn-clean', text: 'Đồng ý', onClick: function($noty) {
                    $noty.close();
                    updateLocation();
                }},
                {addClass: 'btn btn-info btn-clean', text: 'Hủy bỏ', onClick: function($noty) {
                    $noty.close();
                }}
            ]
    });     
}

updateLocation = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/updateLocation',
        data: {
            realLat: document.getElementById('realLat').value,
            realLong: document.getElementById('realLong').value,
            addressId: document.getElementById('addressId').value,
            planId: document.getElementById('planId').value,
            isStart: document.getElementById('isStart').value
        },
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            closeAllMessage();
            successMessage('Ghi lại thành công!');
        }
    });     
}