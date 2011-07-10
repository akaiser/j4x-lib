/*
 * jQuery XOverlay Plugin
 *
 * Extension to overlay an object
 *
 * Copyright 2011, Albert Kaiser
 * under the GPL Version 2 licenses.
 * 
 * @author akaiser
 * @version 1.0 (04.04.11)
 *
 * http://j4x.org
 * 
 * Depends:
 * 
 *  - j4x UI css
 *  jquery.ui.xoverlay.css
 *
 * @param {element} to overlay
 */
(function($) {
    $.xoverlay = function(obj) {

        // the overlay div
        var xoverlay = $('<div id="' + $(obj).attr('id') + '_xoverlay" class="ui-xoverlay" />');

        var offset = null;

        // append after obj
        $(obj).after(xoverlay);

        // public method for de/activate the overlay
        this.show = function(visible) {

            offset = obj.offset();

            // set position of the overlay
            $(xoverlay).css({
                        'width':$(obj).width(),
                        'height':$(obj).height(),
                        'top':offset.top,
                        'left':offset.left
                    });

            // de-/activate
            visible ? $(xoverlay).show() : $(xoverlay).hide();
        };
        return this;
    };
})(jQuery);