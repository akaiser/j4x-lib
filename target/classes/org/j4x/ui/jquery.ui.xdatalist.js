/*
 * jQuery UI XDataList
 *
 * Copyright 2011, Albert Kaiser
 * under the GPL Version 2 licenses.
 *
 * @author akaiser
 * @version 0.8 (21.05.11)
 *
 * http://j4x.org
 *
 * Depends:
 *   jquery.ui.core.js
 *   jquery.ui.widget.js
 *   jquery.ui.button.js
 *
 *   jquery.ui.core.css
 *   jquery.ui.theme.css
 *   jquery.ui.button.css
 *
 *   jquery.delay.js
 *   jquery.json.js
 *   jquery.xoverlay.js
 *
 */
(function($) {
    $.widget( 'ui.xdatalist', {

        _xdatalist: null,
        
        _create: function() {

        },

        _init: function() {
        },

        destroy: function() {
            this._xdatalist.remove();
            $.Widget.prototype.destroy.apply(this, arguments);
        }
    });

    $.extend($.ui.xdatalist, {
        version: '@VERSION'
    });
})(jQuery);