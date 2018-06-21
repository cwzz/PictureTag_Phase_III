
$(document).ready(function () {
    var table = [
        "我", "", "", 5, 1,
        "H", "", "", 6, 1,
        "H", "", "", 7, 1,
        "H", "", "", 12, 1,
        "H", "", "", 13, 1,
        "H", "", "", 14, 1,
        "H", "", "", 4, 2,
        "H", "", "", 5, 2,
        "H", "", "", 6, 2,
        "H", "", "", 7, 2,
        "H", "", "", 8, 2,
        "H", "", "", 11, 2,
        "H", "", "", 12, 2,
        "H", "", "", 13, 2,
        "H", "", "", 14, 2,
        "H", "", "", 15, 2,
        "H", "", "", 3, 3,
        "H", "", "", 4, 3,
        "H", "", "", 5, 3,
        "H", "", "", 6, 3,
        "H", "", "", 7, 3,
        "H", "", "", 8, 3,
        "H", "", "", 9, 3,
        "H", "", "", 10, 3,
        "H", "", "", 11, 3,
        "H", "", "", 12, 3,
        "H", "", "", 13, 3,
        "H", "", "", 14, 3,
        "H", "", "", 15, 3,
        "H", "", "", 16, 3,
        "H", "", "", 3, 4,
        "H", "", "", 4, 4,
        "H", "", "", 5, 4,
        "H", "", "", 6, 4,
        "H", "", "", 7, 4,
        "H", "", "", 8, 4,
        "H", "", "", 9, 4,
        "H", "", "", 10, 4,
        "H", "", "", 11, 4,
        "H", "", "", 12, 4,
        "H", "", "", 13, 4,
        "H", "", "", 14, 4,
        "H", "", "", 15, 4,
        "H", "", "", 16, 4,
        "H", "", "", 3, 5,
        "H", "", "", 4, 5,
        "H", "", "", 5, 5,
        "H", "", "", 6, 5,
        "H", "", "", 7, 5,
        "H", "", "", 8, 5,
        "H", "", "", 9, 5,
        "H", "", "", 10, 5,
        "H", "", "", 11, 5,
        "H", "", "", 12, 5,
        "H", "", "", 13, 5,
        "H", "", "", 14, 5,
        "H", "", "", 15, 5,
        "H", "", "", 16, 5,
        "H", "", "", 4, 6,
        "H", "", "", 5, 6,
        "H", "", "", 6, 6,
        "H", "", "", 7, 6,
        "H", "", "", 8, 6,
        "H", "", "", 9, 6,
        "H", "", "", 10, 6,
        "H", "", "", 11, 6,
        "H", "", "", 12, 6,
        "H", "", "", 13, 6,
        "H", "", "", 14, 6,
        "H", "", "", 15, 6,
        "H", "", "", 5, 7,
        "H", "", "", 6, 7,
        "H", "", "", 7, 7,
        "H", "", "", 8, 7,
        "H", "", "", 9, 7,
        "H", "", "", 10, 7,
        "H", "", "", 11, 7,
        "H", "", "", 12, 7,
        "H", "", "", 13, 7,
        "H", "", "", 14, 7,
        "H", "", "", 6, 8,
        "H", "", "", 7, 8,
        "H", "", "", 8, 8,
        "H", "", "", 9, 8,
        "H", "", "", 10, 8,
        "H", "", "", 11, 8,
        "H", "", "", 12, 8,
        "H", "", "", 13, 8,
        "H", "", "", 7, 9,
        "H", "", "", 8, 9,
        "H", "", "", 9, 9,
        "H", "", "", 10, 9,
        "H", "", "", 11, 9,
        "H", "", "", 12, 9,
        "H", "", "", 9, 10,
        "H", "", "", 10, 10
    ];

    var camera, scene, renderer;
    var controls;

    var objects = [];
    var targets = { table: [], sphere: [], helix: [], grid: [] };
    init();
    animate();
})

function init() {

    camera = new THREE.PerspectiveCamera( 40, window.innerWidth / window.innerHeight, 1, 10000 );
    camera.position.z = 3000;

    scene = new THREE.Scene();

    // table

    for ( var i = 0; i < table.length; i += 5 ) {

        var element = document.createElement( 'div' );
        element.className = 'element';
        element.style.backgroundColor = 'rgba(0,127,127,' + ( Math.random() * 0.5 + 0.25 ) + ')';

        var number = document.createElement( 'div' );
        number.className = 'number';
        number.textContent = (i/5) + 1;
        element.appendChild( number );

        var symbol = document.createElement( 'div' );
        symbol.className = 'symbol';
        symbol.textContent = table[ i ];
        element.appendChild( symbol );

        var details = document.createElement( 'div' );
        details.className = 'details';
        details.innerHTML = table[ i + 1 ] + '<br>' + table[ i + 2 ];
        element.appendChild( details );

        var object = new THREE.CSS3DObject( element );
        object.position.x = Math.random() * 4000 - 2000;
        object.position.y = Math.random() * 4000 - 2000;
        object.position.z = Math.random() * 4000 - 2000;
        scene.add( object );

        objects.push( object );

        //

        var object = new THREE.Object3D();
        object.position.x = ( table[ i + 3 ] * 140 ) - 1330;
        object.position.y = - ( table[ i + 4 ] * 180 ) + 990;

        targets.table.push( object );

    }

    // sphere

    var vector = new THREE.Vector3();

    for ( var i = 0, l = objects.length; i < l; i ++ ) {

        var phi = Math.acos( -1 + ( 2 * i ) / l );
        var theta = Math.sqrt( l * Math.PI ) * phi;

        var object = new THREE.Object3D();

        object.position.x = 800 * Math.cos( theta ) * Math.sin( phi );
        object.position.y = 800 * Math.sin( theta ) * Math.sin( phi );
        object.position.z = 800 * Math.cos( phi );

        vector.copy( object.position ).multiplyScalar( 2 );

        object.lookAt( vector );

        targets.sphere.push( object );

    }

    // helix

    var vector = new THREE.Vector3();

    for ( var i = 0, l = objects.length; i < l; i ++ ) {

        var phi = i * 0.175 + Math.PI;

        var object = new THREE.Object3D();

        object.position.x = 900 * Math.sin( phi );
        object.position.y = - ( i * 8 ) + 450;
        object.position.z = 900 * Math.cos( phi );

        vector.x = object.position.x * 2;
        vector.y = object.position.y;
        vector.z = object.position.z * 2;

        object.lookAt( vector );

        targets.helix.push( object );

    }

    // grid

    for ( var i = 0; i < objects.length; i ++ ) {

        var object = new THREE.Object3D();

        object.position.x = ( ( i % 5 ) * 400 ) - 800;
        object.position.y = ( - ( Math.floor( i / 5 ) % 5 ) * 400 ) + 800;
        object.position.z = ( Math.floor( i / 25 ) ) * 1000 - 2000;

        targets.grid.push( object );

    }

    //

    renderer = new THREE.CSS3DRenderer();
    renderer.setSize( window.innerWidth, window.innerHeight );
    renderer.domElement.style.position = 'absolute';
    document.getElementById( 'container' ).appendChild( renderer.domElement );

    //

    controls = new THREE.TrackballControls( camera, renderer.domElement );
    controls.rotateSpeed = 0.5;
    controls.minDistance = 500;
    controls.maxDistance = 6000;
    controls.addEventListener( 'change', render );

    var button = document.getElementById( 'table' );
    button.addEventListener( 'click', function ( event ) {

        transform( targets.table, 2000 );

    }, false );

    var button = document.getElementById( 'sphere' );
    button.addEventListener( 'click', function ( event ) {

        transform( targets.sphere, 2000 );

    }, false );

    var button = document.getElementById( 'helix' );
    button.addEventListener( 'click', function ( event ) {

        transform( targets.helix, 2000 );

    }, false );

    var button = document.getElementById( 'grid' );
    button.addEventListener( 'click', function ( event ) {

        transform( targets.grid, 2000 );

    }, false );

    transform( targets.table, 5000 );

    //

    window.addEventListener( 'resize', onWindowResize, false );

}

function transform( targets, duration ) {

    TWEEN.removeAll();

    for ( var i = 0; i < objects.length; i ++ ) {

        var object = objects[ i ];
        var target = targets[ i ];

        new TWEEN.Tween( object.position )
            .to( { x: target.position.x, y: target.position.y, z: target.position.z }, Math.random() * duration + duration )
            .easing( TWEEN.Easing.Exponential.InOut )
            .start();

        new TWEEN.Tween( object.rotation )
            .to( { x: target.rotation.x, y: target.rotation.y, z: target.rotation.z }, Math.random() * duration + duration )
            .easing( TWEEN.Easing.Exponential.InOut )
            .start();

    }

    new TWEEN.Tween( this )
        .to( {}, duration * 2 )
        .onUpdate( render )
        .start();

}

function onWindowResize() {

    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();

    renderer.setSize( window.innerWidth, window.innerHeight );

    render();

}

function animate() {

    requestAnimationFrame( animate );

    TWEEN.update();

    controls.update();

}

function render() {

    renderer.render( scene, camera );

}

