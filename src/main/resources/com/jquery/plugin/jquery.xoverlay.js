/*
 * jQuery Overlay Plugin
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
 * @param obj: object to overlay
 */
(function($) {
    $.xoverlay = function(obj){

        // the overlay div
        var overlay = $('<div class="ui-widget-overlay" style="cursor:wait;display:none" />');

        // append to body
        $('body').append(overlay);

        // public method for de/activate the overlay
        this.show = function(visible){

            // set position of the overlay
            $(overlay).css({
                'width':$(obj).attr('offsetWidth'),
                'height':$(obj).attr('offsetHeight'),
                'left':$(obj).attr('offsetLeft'),
                'top':$(obj).attr('offsetTop')
            });

            // de/activate
            visible ? $(overlay).fadeIn(100) : $(overlay).fadeOut(100);
        }
        return this;
    };
})(jQuery);