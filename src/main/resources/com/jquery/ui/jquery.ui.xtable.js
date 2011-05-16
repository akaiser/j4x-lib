/*
 * jQuery UI J4XTable
 *
 * Copyright 2011, Albert Kaiser
 * under the GPL Version 2 licenses.
 *
 * @author akaiser
 * @version 0.8 (03.01.11)
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
 *   jquery.overlay.js
 *
 */
(function($) {
    $.widget( 'ui.xtable', {

        _pe: null,
        _xtable: null,
        _xoverlay: null,
        _selectedRow: null,
        
        _create: function() {
            this._createTable();
            this._createFilter();
        },

        _init: function() {
            
            // init overlay functionality
            this._xoverlay = $.xoverlay(this.element);

            this._restore('INIT',{
                sortpath: this.options.sortpath,
                rowcount: this.options.rowcount,
                filters: this.options.filter
            });
        },

        _createTable: function() {
            var self = this;
            var o = self.options;

            /**
             * header creation
             */
            var headerRow = $('<tr />');

            $(o.column).each(function(i,e){

                var column = $('<th />');
                var button = $('<button style="width:100%">'+e.label+'</button>').button();

                // check for width
                if(e.width){
                    $(column).attr('width',e.width);
                }

                // check for sortable
                if(e.sortpath){
                    $(button).attr('id',e.sortpath);

                    $(button).click(function() {

                        // perform sort-event
                        self._restore('SORT', {
                            sortpath: e.sortpath
                        });
                    });

                }else{
                    $(button).button('disable');
                }

                headerRow.append(column.append(button));
            });
            
            /**
             * footer creation
             */

            var paging = (o.paging)?true:false;

            // pagiator elements
            this._pe = [
            $('<span title="INFO" style="float:left; padding:6px" />'),
            $('<button style="margin-right:0px" title="FIRST" />').button({
                label: (paging && o.paging.first)?o.paging.first:'first',
                icons:{
                    primary: 'ui-icon-arrowthickstop-1-w'
                }
            }),
            $('<button title="PREVIOUS" />').button({
                label: (paging && o.paging.previous)?o.paging.previous:'previous',
                icons:{
                    primary: 'ui-icon-arrowthick-1-w'
                }
            }),
            $('<button title="NEXT" />').button({
                label: (paging && o.paging.next)?o.paging.next:'next',
                icons:{
                    secondary: 'ui-icon-arrowthick-1-e'
                }
            }),
            $('<button title="LAST" />').button({
                label: (paging && o.paging.last)?o.paging.last:'last',
                icons:{
                    secondary: 'ui-icon-arrowthickstop-1-e'
                }
            }),
            $('<button title="RELOAD" />').button({
                label: (paging && o.paging.reload)?o.paging.reload:'reload'
            })
            ];

            // container for paging elements
            var pagingDiv = $('<div align="right" />');

            // fill the paging container
            $(this._pe).each(function(i,e){

                // only buttons are click- and removeable
                if(i!=0){
                    $(e).click(function(){

                        // perform paging-event
                        self._restore('PAGING', {
                            event: $(e).attr('title')
                        });
                    });

                    if(!o.rowcount){
                        $(e).css('display','none');
                    }
                }
                $(pagingDiv).append(e);
            });

            // button ui fixing
            $('button',pagingDiv).css('margin-right','0px');

            /**
             * table creation
             */
            this._xtable = $(
                '<table class="ui-widget" style="width:100%" cellpadding="0" cellspacing="0">'
                +'<thead class="ui-widget-header"></thead>'
                +'<tbody class="ui-widget-content"></tbody>'
                +'<tfoot class="ui-widget-header"></tfoot>'
                +'</table>');

            // append head and foot
            $('thead',this._xtable).append(headerRow);
            $('tfoot',this._xtable).append($('<tr />')
                .append($('<td />').attr('colspan', $('th', headerRow).length)
                    .append(pagingDiv)));

            // finally append table to this element
            this.element.append(this._xtable);
        },

        _createFilter: function() {
            var self = this;

            // prepare filter
            var filterElement, filterValue;

            $(this.options.filter).each(function(i,e){

                filterElement = $('#'+e.id);
                filterValue = $.trim($(filterElement).val());

                // identify filtertype and bind event
                switch($(filterElement).attr('tagName')){
                    case 'INPUT':

                        // initial param setup
                        e.filtertype = 'INPUT';

                        // KeyUp or OnChange
                        if(e.keyup){
                            $(filterElement).delay('keyup', function() {
                                self._performInputEvent(e,this);
                            },300);
                        }else{
                            $(filterElement).change(function(){
                                self._performInputEvent(e,this);
                            });
                        }

                        // label attr is available
                        if(e.label){

                            // bind events for label setup
                            $(filterElement).blur(function(){
                                if($.trim($(this).val()).length==0){
                                    $(this).val(e.label).css('color','#aaa');
                                //$(this).val(e.label).addClass('ui-state-error-text');
                                }
                            }).focus(function(){
                                if($.trim($(this).val())==e.label){
                                    $(this).val('').css('color','');
                                //$(this).val('').removeClass('ui-state-error-text');
                                }
                            });

                            // check the init value (firefox reload fix)
                            if(e.label != filterValue){

                                // set the (maybe defined) init value
                                e.filtervalue = filterValue;
                            }
                        }else{

                            // set the (maybe defined) init value
                            e.filtervalue = filterValue;
                        }

                        // @todo: hier soll irgendwie Suggest-Implementierung stattfinden,


                        /*
                        // some ui settings?
                        $(filterElement).addClass('ui-state-default')
                        .focus(function(){
                            $(this).addClass('ui-state-highlight');
                        })
                        .blur(function(){
                            $(this).removeClass('ui-state-highlight');
                        });
                        */
                        //e.filtertype = 'SUGGEST';
                        break;
                    case 'SELECT':
                        if($(filterElement).attr('multiple')){
                            e.filtertype = 'SELECTMULTIPLE';
                        }else{
                            e.filtertype = 'SELECTONE';

                            $(filterElement).change(function(){
                                e.filtervalue = $.trim($(this).val());
                                //e.filtervalue = $.trim($(this).attr('value'));
                                self._restore('FILTER', e);
                            });
                           
                        //interesting for multiselect
                        //var str = '';
                        //$('#'+this[0]+' option:selected').each(function () {
                        //str += $(this).text() + ' ';
                        //});
                        }
                        break;
                }
            });
        },

        _performInputEvent: function(filter,element) {

            var filterValue = $.trim($(element).val());
            var valueLength = filterValue.length;

            // more than two char and diff. value
            if((valueLength == 0 || valueLength > 1)
                && (filter.filtervalue != filterValue)){

                if(!filter.label || (filter.label && filter.label != filterValue)){
                    filter.filtervalue = filterValue;
                    this._restore('FILTER', filter);
                }
                $(element).removeClass('ui-state-error-text');
            }else{
                $(element).addClass('ui-state-error-text');
            }
        },

        _restore: function(requestType, requestParams) {
            var self = this;

            // show overlay
            self._xoverlay.show(true);

            // perform remote request
            self.options.datasource.getTableContent(requestType,
                $.toJSON(requestParams), function(responce){
                
                    self._restoreHeader(responce.sort);
                    self._restoreBody(responce.body);
                    self._restoreFooter(responce.paging);
                    self._restoreFilter(responce.filter, requestType, requestParams.event);

                    // hide overlay
                    self._xoverlay.show(false);
                });
        },

        // @todo describe params
        _restoreHeader: function(params) {
            var self = this;

            // header-columns of the table
            var headerColumns = $('thead th', this._xtable);

            $(headerColumns).each(function(i,e){

                // identify the nested button
                var currentButton = $(e).find('button');

                // sorting only possible with multiple entrys
                if(params[0] > 1){

                    var path = $(currentButton).attr('id');

                    // sorting only possible if there is a path
                    if(path){

                        // enable the button
                        self._toggleButtonStatus(currentButton, true);

                        // current column is responsible for sorting
                        if(params[1] != null && path == params[1]){

                            // add correct sort icon
                            if(params[2]){
                                $(currentButton).button('option','icons',{
                                    secondary:'ui-icon-carat-1-s'
                                });
                            }else{
                                $(currentButton).button('option','icons',{
                                    secondary:'ui-icon-carat-1-n'
                                });
                            }
                        }else{

                            // handle other columns/buttons and remove the icon
                            if($(currentButton).button('option','icons').secondary){
                                $(currentButton).button('option','icons',{
                                    secondary:null
                                });
                            }
                        }
                    }
                }else{

                    // disable the button
                    self._toggleButtonStatus(currentButton, false);
                }

            // remove hover and focus
            //self._removeHoverAndFocus(currentButton);
            });
        },

        // @todo describe params
        _restoreBody: function(params) {
            var self = this;

            // create a table body
            var tableBody = $('<tbody class="ui-widget-content" />');

            // iterate each row entry
            $(params).each(function(){

                // create new row with hover and click event
                var newRow = $('<tr style="cursor:pointer" />')
                .hover(function(){
                    $(this).toggleClass('ui-state-hover');
                })
                .click(function(){

                    // switch selected row
                    $(self._selectedRow).css({
                        'cursor':'pointer',
                        'text-shadow':''
                    });
                    self._selectedRow = $(newRow).css({
                        'cursor':'default',
                        'text-shadow':'1px 1px 1px #666'
                    });

                //$(self._selectedRow).removeClass('ui-state-focus').css('cursor','pointer');
                //self._selectedRow = $(newRow).addClass('ui-state-focus').css('cursor','default');
                });

                // iterate each column
                $(this).each(function(i,e){

                    // append the object id
                    if(i==0){
                        $(newRow).attr('id', e);
                    }else{
                        var newCell = $('<td>'+e+'</td>');

                        $(newRow).append(newCell);
                    }
                });

                // some cell border... #E6E6E6
                $('td:first',newRow).css('border-left','1px solid #D3D3D3');
                $('td:last',newRow).css({
                    'padding':'2px',
                    'border-right':'1px solid #D3D3D3'
                });

                // append row to the body
                $(newRow).appendTo(tableBody);
            });

            // add alternate row background
            $('tr:odd', tableBody).addClass('ui-state-default');

            // replace current body
            $('tbody', self._xtable).replaceWith(tableBody);
        },

        // @todo describe params
        _restoreFooter: function(params) {
            var self = this;

            $(params).each(function(i,e){
                
                // get the info
                if(i==0){
                    $(self._pe[i]).text(e);

                // do work with the buttons
                }else{

                    // remove hover and focus
                    self._removeHoverAndFocus(self._pe[i]);

                    // de/activate the buttons
                    self._toggleButtonStatus(self._pe[i], e);
                }
            });

            // also for refresh-button
            self._removeHoverAndFocus(self._pe[5]);
        },

        // @todo describe params
        _restoreFilter: function(params, requestType, eventType) {
            var self = this;

            $(params).each(function(i,e){

                // Element de/aktivieren
                /*
                        if(this[1].length < 2){
                            $(selectElement).attr('disabled', 'disabled');
                        }else{
                            $(selectElement).removeAttr('disabled');
                        }
                        */
                // Das Select-Element
                var filterElement = $('#' + e[0]);

                // Unterscheidung des Typs
                switch(e[1]){

                    // Ein Text-Element, setzen der Werte (IE und Chrome)
                    case 'INPUT':

                        // updating only on INIT or PAGING-RELOAD event
                        if(requestType == 'INIT' || eventType == 'RELOAD'){
                            $(filterElement).val(e[2]);

                            // perform post init and reload actions on filter
                            var filterValue;
                            $(self.options.filter).each(function(i,f){
                                if(f.label && f.id == e[0]){
                                    
                                    filterValue = $.trim($(filterElement).val());

                                    if(filterValue.length == 0){
                                        $(filterElement).val(f.label).css('color','#aaa');
                                    }
                                }
                            });
                        }

                        break;

                    // Ein Select-Element, setzen der selected-option
                    case 'SELECTONE': {

                        var options = '';
                        var selectedValues = e[3];
                        var selectedAttribute;
                        var slelected = false;

                        $(e[2]).each(function() {

                            var value = this.toString();

                            selectedAttribute = '';

                            if(value == selectedValues){
                                selectedAttribute = 'selected="selected" ';
                                slelected = true;
                            }

                            // create options
                            options += '<option '+selectedAttribute+'value="'+value+'">'+ value +'</option>';
                        });

                        if(slelected){
                            selectedAttribute = '';
                        }else{
                            selectedAttribute = 'selected="selected" ';
                        }

                        options = '<option '+selectedAttribute+'></option>'+options;

                        // remove present options
                        $('option', filterElement).remove();

                        // add new options
                        $(filterElement).append($(options));

                        break;
                    }

                    // @todo
                    case 'SELECTMULTIPLE': {
                        break;
                    }
                }
            });
        },

        
        // Helper for removing hover and focus
        _removeHoverAndFocus: function(obj) {
            if($(obj).hasClass('ui-state-hover')){
                $(obj).removeClass('ui-state-hover');
            }

            if($(obj).hasClass('ui-state-focus')){
                $(obj).removeClass('ui-state-focus');
            }
        },

        // Helper to en/disable a button
        _toggleButtonStatus: function(obj, enable) {
            if(enable==true){
                if($(obj).button('option','disabled')){
                    $(obj).button('enable');
                }
            }else{
                if(!$(obj).button('option','disabled')){
                    $(obj).button('disable');
                }
            }
        },

        destroy: function() {
            this._xtable.remove();
            $.Widget.prototype.destroy.apply(this, arguments);
        }
    });

    $.extend($.ui.xtable, {
        version: '@VERSION'
    });
})(jQuery);