// Global H2 namespace
window.h2 = window.h2 || {};
window.h2.CaptureEmailVoucherCallToAction = (function() {
  'use strict';

  var action = '//hassle.us9.list-manage.com/subscribe/post?u=a0eca7d2832f801e55041e747&id=9568aa1968';
  var $form;
  var $giftButton;
  var $input;
  var $submit;
  var voucherCode;

  function showVoucherCode() {
    $submit.css('opacity', 0);
    $giftButton.css('right', '-400px');
    $input.attr('disabled', true)
      .css('color', 'transparent');

    setTimeout(function() {
      $submit.attr('disabled', true);
      $input.val(voucherCode)
        .css('color', '');
    }, 500);
  }

  function handleFormSubmit(e) {
    e.preventDefault();

    $.post(action, {
      EMAIL: $input.val()
    }).always(showVoucherCode);
  }

  function init() {
    $form = $('.js-lp-show-voucher-code');
    $input = $form.find('.js-lp-show-voucher-code-input');
    $submit = $form.find('.capture-email-voucher__submit');
    $giftButton = $form.find('.capture-email-voucher__gift-button');
    voucherCode = $input.data('voucher-code');

    $form.on('submit', handleFormSubmit);
  }

  return {
    init: init
  }
})();



window.h2.Slideshow = (function () {
    "use strict";

    var config = {
        selector: '.slideshow-teaser',
        duration: 1000
    };

    var slider = {
        element : undefined,
        ul      : undefined,
        slides  : undefined,
        active  : false,
        current : 0,
        nextBtn : undefined,
        prevBtn : undefined
    };

    var dimensions = {
        parent: 0
    };

    var init = function () {
        var sliderOffset;

        slider.element = document.querySelector(config.selector);

        if (!slider.element) {
            return false;
        }

        sliderOffset = slider.element.offsetTop - slider.element.offsetHeight - 200;

        // only start loading slider when the user scrolls near it
        function lazySlider() {

            if (window.scrollTop > sliderOffset || window.scrollY > sliderOffset || document.documentElement.scrollTop > sliderOffset) {
                setup();
                window.removeEventListener('scroll', lazySlider);
            }
        }

        window.addEventListener('scroll', lazySlider);
        lazySlider();
    };

    var setup = function () {
        // get dom elements
        slider.ul = document.querySelector(config.selector + '> ul');
        slider.slides = document.querySelectorAll(config.selector + '> ul > li');
        slider.nextBtn = document.querySelector(config.selector + ' > .icon-slider-right');
        slider.prevBtn = document.querySelector(config.selector + ' > .icon-slider-left');

        if (!slider.ul) {
            return;
        }

        // enable slide images to load
        for (var i = 0; i < slider.slides.length; i++) {
            slider.slides[i].setAttribute('style', slider.slides[i].getAttribute('data-background'));
        }

        // listen for size changes
        window.addEventListener('resize', function () {
            updateDimensions();
        });
        updateDimensions();

        // make interactive
        bindNavigation();
    };

    // enable controls
    var bindNavigation = function () {
        slider.nextBtn.addEventListener('click', function () {
            slide(slider.current + 1);
        });

        slider.prevBtn.addEventListener('click', function () {
            slide(slider.current - 1);
        });

        updateNavigation();
    };

    // set target position (animated via css transition)
    var slide = function (targetSlide) {
        if (targetSlide > slider.slides.length - 1 || targetSlide < 0) {
            return;
        }
        slider.current = targetSlide;

        updateNavigation();

        slider.ul.style.marginLeft = (dimensions.parent * slider.current * -1) + 'px';
    };

    // disable buttons at far ends of slider
    var updateNavigation = function () {
        slider.nextBtn.className = slider.nextBtn.className.replace(' disabled', '');
        slider.prevBtn.className = slider.prevBtn.className.replace(' disabled', '');
        if (slider.current === slider.slides.length - 1) {
            slider.nextBtn.className += ' disabled';
        }

        if (slider.current === 0) {
            slider.prevBtn.className += ' disabled';
        }
    };

    // keep full-page width intact on resize
    var updateDimensions = function () {
        dimensions.parent = parseInt(slider.element.parentNode.offsetWidth);

        for (var i = 0; i < slider.slides.length; i++) {
            slider.slides[i].style.width = dimensions.parent + 'px';
        }

        slider.ul.className = 'no-animation';
        slider.ul.style.width = (dimensions.parent * slider.slides.length) + 'px';
        slider.ul.style.marginLeft = (dimensions.parent * slider.current * -1) + 'px';
        slider.ul.className = '';
    };

    return {
        init: init
    };
})();

window.h2.MarketingCallToAction = (function () {
    "use strict";

    function _highlightZipcode (e) {
        var $postcodeInput = ($("#bid_postcode").length) ? $("#bid_postcode") : $("#postcode");
        if (!$("#main-teaser").length || !$postcodeInput.length) {
            return;
        }

        e.preventDefault();

        $('html, body').animate({
            scrollTop: $("#main-teaser").offset().top
        }, 500);

        $postcodeInput.focusout();
        setTimeout(function(){
            $postcodeInput.focus()
        }, 400);
    }

    function init () {
        $('.actions .btn[data-checkout="true"], .actions .btn.provider-signup-open-link').click(_highlightZipcode);
    }

    return {
        init: init
    };
})();

$(document).ready(function() {
  window.h2.Slideshow.init();
  window.h2.MarketingCallToAction.init();
  window.h2.CaptureEmailVoucherCallToAction.init();
});
