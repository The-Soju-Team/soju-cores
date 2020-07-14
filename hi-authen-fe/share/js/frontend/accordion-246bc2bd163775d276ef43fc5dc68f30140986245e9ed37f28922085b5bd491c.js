window.Hassle.Accordion = (function () {
  "use strict";

  var toggleClass = function (element, className){
    var regEx, classString, nameIndex;
    if (!element || !className){
      return;
    }
    classString = element.className;
    nameIndex = classString.indexOf(className);
    
    if (nameIndex === -1) {
      classString += ' ' + className;
    }
    else {
      regEx = new RegExp('[ ]*' + className + '[ ]*');
      classString = classString.replace(regEx, '');
    }
    element.className = classString; // eslint-disable-line no-param-reassign
  };

  var parents = function(element, className) {
    if (element.className === className) {
      return element;
    }
    return parents(element.parentNode, className);
  };

  var toggleAccordion = function(target, single, accordionHeaders) {
    var parent = parents(target, 'panel'),
      body = parent.querySelectorAll('.panel-heading')[0];

    if (single) {
      accordionHeaders.forEach(function(accordionHeader) {
        if (accordionHeader.className.indexOf('active') !== -1) {
          toggleClass(accordionHeader, 'active');
        }
      });
    }

    toggleClass(body, 'active');
  };

  var init = function () {
    var accordions = Array.apply(null, document.querySelectorAll('.panel-group'));

    accordions.forEach(function(accordion) {
      var accordionHeaders = Array.apply(null, accordion.querySelectorAll('.panel-heading')),
          attribs = [],
          index,
          dataName,
          attribute,
          bodies,
          body;

      for (index = 0; index < accordion.attributes.length; index++) {
        attribute = accordion.attributes[index];
        if (attribute.name.match(/^data/)) {
          dataName = attribute.name.substr(5);
          attribs[dataName] = accordion.attributes[attribute.name].value;
        }
      }
      if (attribs.active) {
        bodies = accordion.getElementsByClassName('panel-heading');
        body = bodies[attribs.active - 1];
        toggleClass(body, 'active');
      }

      accordionHeaders.forEach(function(accordionHeader) {
        accordionHeader.addEventListener('click', function(e) {
          toggleAccordion(e.target, attribs.single, accordionHeaders);
        });
      });
    });
  };

  return {
    init: init
  };
})();
