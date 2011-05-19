(function(a){
    if(typeof console==="undefined"){
        console=typeof window.console!=="undefined"?window.console:{}
    }
    console.log=console.log||function(){};

    console.debug=console.debug||console.log;
    console.warn=console.warn||console.log;
    console.error=console.error||function(){
    var b=[];
    for(var c=0;c<arguments.length;c++){
        b.push(arguments[c])
        }
        alert(b.join("\n"))
    };

console.trace=console.trace||console.log;
console.group=console.group||console.log;
console.groupEnd=console.groupEnd||console.log;
console.profile=console.profile||console.log;
console.profileEnd=console.profileEnd||console.log;
a.History={
    options:{
        debug:false
    },
    state:"",
    $window:null,
    $iframe:null,
    handlers:{
        generic:[],
        specific:{}
},
format:function(b){
    b=b.replace(/^.+?#/g,"").replace(/^#?\/?|\/?$/g,"");
    return b
    },
getState:function(){
    var b=a.History;
    return b.state
    },
setState:function(c){
    var b=a.History;
    c=b.format(c);
    b.state=c;
    return b.state
    },
getHash:function(){
    var b=a.History;
    var c=window.location.hash||location.hash;
    c=b.format(c);
    return c
    },
setHash:function(c){
    var b=a.History;
    c=a.History.format(c);
    c=c.replace(/^\/?|\/?(\?)|\/?$/g,"/$1");
    if(typeof window.location.hash!=="undefined"){
        window.location.hash=c
        }else{
        location.hash=c
        }
        if(a.browser.msie&&parseInt(a.browser.version,10)<8){
        a.History.$iframe.contentWindow.document.open();
        a.History.$iframe.contentWindow.document.close();
        a.History.$iframe.contentWindow.document.location.hash=state
        }
    },
go:function(c){
    var b=a.History;
    c=b.format(c);
    var d=b.getHash();
    if(d!==c){
        b.setHash(c)
        }else{
        b.setState(c);
        b.trigger()
        }
        return true
    },
hashchange:function(f){
    var b=a.History;
    if(b.options.debug){
        console.debug("History.hashchange",this,arguments)
        }
        var d=b.getHash();
    var c=b.getState();
    if((!b.$iframe&&c===d)||(b.$iframe&&b.hash===b.$iframe.contentWindow.document.location.hash)){
        return false
        }
        if(c===d){
        return false
        }
        b.setState(d);
    b.trigger();
    return true
    },
bind:function(d,b){
    var c=a.History;
    if(b){
        if(typeof c.handlers.specific[d]==="undefined"){
            c.handlers.specific[d]=[]
            }
            c.handlers.specific[d].push(b)
        }else{
        b=d;
        c.handlers.generic.push(b)
        }
        return true
    },
trigger:function(f){
    var d=a.History;
    if(typeof f==="undefined"){
        f=d.getState()
        }
        var b,g,c,e;
    if(typeof d.handlers.specific[f]!=="undefined"){
        e=d.handlers.specific[f];
        for(b=0,g=e.length;b<g;++b){
            c=e[b];
            c(f)
            }
        }
        e=d.handlers.generic;
for(b=0,g=e.length;b<g;++b){
    c=e[b];
    c(f)
    }
    return true
},
construct:function(){
    var b=a.History;
    a(document).ready(function(){
        b.domReady()
        });
    return true
    },
configure:function(b){
    var c=a.History;
    c.options=a.extend(c.options,b);
    return true
    },
domReadied:false,
domReady:function(){
    var b=a.History;
    if(b.domRedied){
        return
    }
    b.domRedied=true;
    b.$window=a(window);
    b.$window.bind("hashchange",this.hashchange);
    setTimeout(b.hashchangeLoader,200);
    return true
    },
hashchangeLoader:function(){
    var c=a.History;
    if(!(a.browser.msie&&parseInt(a.browser.version)>=8)){
        var b;
        if(a.browser.msie){
            c.$iframe=a('<iframe id="jquery-history-iframe" style="display: none;"></$iframe>').prependTo(document.body)[0];
            c.$iframe.contentWindow.document.open();
            c.$iframe.contentWindow.document.close();
            var d=c.getHash();
            if(d){
                c.$iframe.contentWindow.document.location.hash=d
                }
                b=function(){
                var e=c.format(c.$iframe.contentWindow.document.location.hash);
                if(c.getState()!==e){
                    c.setHash(c.$iframe.contentWindow.document.location.hash)
                    }
                    var f=c.getHash();
                if(c.getState()!==f){
                    c.go(f)
                    }
                }
        }else{
    b=function(){
        var e=c.getHash();
        if(c.getState()!==e){
            c.go(e)
            }
        }
}
setInterval(b,200)
}else{
    var d=c.getHash();
    if(d){
        c.$window.trigger("hashchange")
        }
    }
return true
}
};

a.History.construct()
})(jQuery);