searchApp = function() {
    $('#appTable').DataTable( {
        destroy: true,
        processing: false,
        serverSide: true,
        searching: false,    
        bSort: false,       
        ajax: {
            type: 'POST',
            dataType: 'json',
            url: '/authen/app',
            data: function ( d ) {
                d['hi-process'] = 'app';
                d.cmd = 'SEARCH_APP';
                d.appname = $('#s_appname').val();
                d.appcode = $('#s_appcode').val();
                if(document.getElementById('isdelete').value == '1') {
                    d.isdelete = '1';
                    d.appid = $('#appTable_wrapper :input[type="checkbox"]').serialize();
                }
                else {
                    d.isdelete = '0';
                }
            },
            cache: false
        },
        initComplete: function(settings, json) {
            if ('TIMEOUT' == request.response_code) {
                    console.log("Quá hạn đăng nhập, mời đăng nhập lại - TIMEOUT");
                    logout();
            } else {
                document.getElementById('sso_user_name').innerHTML = request.sso_user_name;
            }            
            if(document.getElementById('isdelete').value == '1') {
                successMessage('Xóa thành công!');
            }
            document.getElementById('isdelete').value = '0';
            validateCheckAll();
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

checkAll = function (obj) {
    var arrCheckbox = document.getElementsByName('appid');
    for(var i = 0; i < arrCheckbox.length; i++) {
        arrCheckbox[i].checked = obj.checked;
    }
}

validateCheckAll = function() {
    var arrCheckbox = document.getElementsByName('appid');
    var checkboxAll = document.getElementById('selectAll');
    if(checkboxAll) {
        checkboxAll.checked = true;
        for(var i = 0; i < arrCheckbox.length; i++) {
            if(!arrCheckbox[i].checked) {
                checkboxAll.checked = false;
                break;
            }
        }    
    }
}

deleteApp = function() {
    document.getElementById('isdelete').value = '1';
    searchApp();
}

deleteOnClick = function() {
    var arrCheckbox = document.getElementsByName('appid');
    var check = false;
    for(var i = 0; i < arrCheckbox.length; i++) {
        if(arrCheckbox[i].checked) {
            check = true;
            break;
        }
    }
    if(check) confirmDelete();
    else errorMessage('Bạn chưa chọn bản ghi để xóa!');
}

confirmDelete = function() {
    noty({
        text: 'Bạn có muốn xóa không?',
        layout: 'center',
        type: 'info',
        buttons: [
                {addClass: 'btn btn-info btn-clean', text: 'Đồng ý', onClick: function($noty) {
                    $noty.close();
                    deleteApp();
                }},
                {addClass: 'btn btn-info btn-clean', text: 'Hủy bỏ', onClick: function($noty) {
                    $noty.close();
                }}
            ]
    });    
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

addApp = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/authen/app',
        data: $("#addForm").serialize(),
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            closeAllMessage();
            searchApp();
            if(document.getElementById('cmd') == 'ADD_APP') {
                successMessage('Thêm mới thành công!');
                clearForm();
            } else {
                successMessage('Cập nhật thành công!');
            }
        }
    });
}

validateClient = function() {
    closeAllMessage();
    if(document.getElementById('appname').value == null || document.getElementById('appname').value.trim().length == 0) {
        showFieldError('appname','Hãy nhập tài khoản');
        return false;
    } else if(document.getElementById('password').value == null || document.getElementById('password').value.trim().length == 0) {
        showFieldError('password','Hãy nhập Mật khẩu');
        return false;
    }   
    return true;
}

closeAllMessage = function() {
    $.noty.closeAll();
    closeAllFieldError();
}

showFieldError = function(fieldId, message) {
    document.getElementById(fieldId).className = 'form-control error';
    document.getElementById(fieldId + '-require').style.display = '';
    document.getElementById(fieldId + '-require').innerHTML = message;
}

closeAllFieldError = function() {
    var arrControl = document.getElementsByClassName('form-control');
    var arrErrorLabel = document.getElementsByClassName('error');
    for(var i = 0; i < arrControl.length; i++) arrControl[i].className = 'form-control';
    for(var i = 0; i < arrErrorLabel.length; i++) arrErrorLabel[i].style.display = 'none';
}

clearForm = function() {
    document.getElementById('appname').value = '';
    document.getElementById('appcode').value = '';
    document.getElementById('ip').value = '';
    document.getElementById('port').value = '';
}

loadViewApp = function(viewAppId) {
    $('#selectModal').modal().show();
    document.getElementById('cmd').value = 'UPDATE_APP';
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/authen/app',
        data: {appid: viewAppId, 'hi-process': 'app', cmd: 'LOAD_VIEW_APP'},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            if(data.app_id) document.getElementById('appid').value = data.app_id;
            if(data.app_name) document.getElementById('appname').value = data.app_name;
            if(data.app_code) document.getElementById('appcode').value = data.app_code;
            if(data.ip) document.getElementById('ip').value = data.ip;
            if(data.port) document.getElementById('port').value = data.port;
        }
    });    
}

function viewAddApp() {
    document.getElementById('cmd').value = 'ADD_APP';
    clearForm();
    $('#selectModal').modal().show();
}


var cancelValidate = false;
var progressCount = 100;
doLogin = function(loginToken) {
	if(!validateClient()) return;
	closeAllMessage();
	var forward = window.location.href.indexOf("hi-process");
	if(forward == -1) forward = 0;
	$.ajax({ 
		type: 'POST',
		dataType: 'json',
		url: '/authen',
		data: {
			'hi-process': 'login',
			'userName': document.getElementById('userName').value,
			'password': document.getElementById('password').value,
			'captcha': document.getElementById('captcha').value,
			'callback': document.getElementById('callback').value,
			'forward' : forward,
			loginToken: loginToken
		},
		cache: false, //fix loop IE
		success: function(request, textStatus, jqXHR) {
			closeAllMessage();
			if (request.status == '0') { // dang nhap thanh cong
				var callBack = request.call_back;
				if(callBack != undefined && callBack != null && callBack != '') {
					window.location.href = callBack;
				}
				else
					window.location.href = '/authen/home';
			} else {
				document.getElementById('captcha').value = '';
				document.getElementById('loginButton').style.display = '';
				// if (request.status == '1') {
				// 	document.getElementById('userName').focus();
				// 	//console.log('userName', document.getElementById('userName').value)
				// 	if (document.getElementById('userName').value == 'visual_admin') {
				// 			showErrorUserName('Tài khoản visual_admin đã hết hiệu lực. Vui lòng liên hệ thuybtb1@viettel.com.vn để được cấp tài khoản đăng nhập vào hệ thống.');
				// 	} else {
				// 			showErrorUserName('Thông tin đăng nhập không chính xác. ');
				// 	}
				// } else if (request.status == '2') {
				// 	document.getElementById('captcha').focus();
				// 	showErrorCaptcha('Sai Mã bảo vệ');
				// } else if (request.status == '3') {
				// 	document.getElementById('userName').focus();
				// 	showErrorUserName('Hãy nhập Tên đăng nhập');
				// } else if (request.status == '4') {
				// 	document.getElementById('password').focus();
				// 	showErrorMessage('Hãy nhập Mật khẩu');
				// } else if (request.status == '5') {
				// 	setTimeout(function() {document.getElementById('captcha').focus();},500);
				// 	showErrorCaptcha('Hãy nhập Mã bảo vệ');
				// } else if (request.status == '6') {
				// 	showErrorUserName('Tài khoản của bạn không được đăng nhập tại IP này');
                // }
                showErrorMessage(request.message)
				if (parseInt(request.failCount) > 2) {
					document.getElementById('captchaLabel').style.display = '';
					document.getElementById('captchaField').style.display = '';
					document.getElementById('buttonArea').style.marginTop = '10px'
					refreshCaptcha();
				}
			}
		}
	});
}

submitLogin = function () {
	$.ajax({
		url: 'http://10.58.244.170:8448/login-token?userName=' + document.getElementById('userName').value,
		success: function (res) {
			console.log(res);
			if (res.loginToken) {
				console.log('loginToken', res.loginToken);
				doLogin(res.loginToken)
			} else {
				alert('Không thể đăng nhập. Vui lòng thử lại sau')
			}
		},
		error: function (err) {
			console.log(err);
			alert('Không thể đăng nhập. Vui lòng thử lại sau.')
		}
	})
}

validateClient = function() {
    closeAllMessage();

	if(document.getElementById('userName').value == null || document.getElementById('userName').value.trim().length == 0) {
		showErrorUserName('Hãy nhập Tên đăng nhập');
		return false;
	} else if(document.getElementById('userName').value == null || document.getElementById('userName').value.trim().length == 0) {
		showErrorUserName('Hãy nhập Mật khẩu');
		return false;
	}

    return true;
}

showErrorMessage = function(message) {
    document.getElementById('errorMessage').innerHTML = message;
    document.getElementById('errorMessage').style.display = '';
}

showErrorUserName = function(message) {
    document.getElementById('userNameError').innerHTML = message;
    document.getElementById('spanUser').style.borderColor = "#E04B4A";
    document.getElementById('userNameError').style.display = '';
}

showErrorPassword = function(message) {
    document.getElementById('passwordError').innerHTML = message;
    document.getElementById('spanPassword').style.borderColor = "#E04B4A";
    document.getElementById('passwordError').style.display = '';
}

showErrorCaptcha = function(message) {
    document.getElementById('captchaError').innerHTML = message;
    document.getElementById('captcha').style.borderColor = "#E04B4A";
    document.getElementById('captchaError').style.display = '';
}

showSuccessMessage = function(message) {
    document.getElementById('successMessage').innerHTML = message;
    document.getElementById('successMessage').style.display = '';    
}

closeAllMessage = function() {
    document.getElementById('errorMessage').style.display = 'none';
    document.getElementById('successMessage').style.display = 'none';
    document.getElementById('userNameError').style.display = 'none';
	document.getElementById('passwordError').style.display = 'none';
    document.getElementById('captchaError').style.display = 'none';
    document.getElementById('spanUser').style.borderColor = "#bbb";
    document.getElementById('captcha').style.borderColor = "#bbb";
}

refreshCaptcha = function() {
	$.ajax({ 
		type: 'POST',
		dataType: 'json',
		url: '/authen',
		data: {
			'hi-process': 'get-captcha'
		},
		cache: false, //fix loop IE
		success: function(request, textStatus, jqXHR) {
			document.getElementById('captchaImg').style.display = '';
			document.getElementById('captchaImg')
				.setAttribute(
					'src', 'data:image/png;base64,' + request.data
				);
		}
	});	
}

loadPage = function() {
	document.getElementById('headerLogo').style.top = '' + Math.round(window.innerHeight/2 - 160) + 'px';
	document.getElementById('loginForm').style.top = '' + Math.round(window.innerHeight/2 - 240) + 'px';		
	/*$.ajax({ 
		type: 'POST',
		dataType: 'json',
		url: '/authen',
		data: {
			'hi-process': 'get-call-back'
		},
		cache: false, //fix loop IE
		success: function(request, textStatus, jqXHR) {
			document.getElementById('callback').value = request.callback;
		}
	});	*/
}
