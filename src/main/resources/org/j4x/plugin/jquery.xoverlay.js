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
        //var overlay = $('<div id="'+$(obj).attr('id')+'_overlay" class="ui-widget-overlay" style="cursor:wait;display:none"><div style="position: absolute; top: 50%; width:100%;  text-align: center;">TRANSFER</div></div>');

        var overlay = $('<div id="'+$(obj).attr('id')+'_overlay" class="ui-widget-overlay" style="cursor:wait;display:none" />');

        var offset = null;

        // append after obj
        $(obj).after(overlay);

        // public method for de/activate the overlay
        this.show = function(visible){

            offset = obj.offset();

            // set position of the overlay
            $(overlay).css({
                'width':$(obj).width(),
                'height':$(obj).height(),
                'top':offset.top,
                'left':offset.left
            });

            // de/activate
            visible ? $(overlay).fadeIn(100) : $(overlay).fadeOut(100);
        }
        return this;
    };
})(jQuery);