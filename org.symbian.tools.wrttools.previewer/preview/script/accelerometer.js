function RotationSupport(accelerationCallback) {
	this.controls = new RotationControls(accelerationCallback);
}

function RotationControls(accelCallback) {
	this.angleX = 0;
	this.angleY = 0;
	this.angleZ = 0;
	var control = this;
	this.accelerationCallback = accelCallback;

	setupListeners("xaxis", "xleft", "xright", updateAngleX, this.angleX);
	setupListeners("yaxis", "yleft", "yright", updateAngleY, this.angleY);
	setupListeners("zaxis", "zleft", "zright", updateAngleZ, this.angleZ);

	window.setTimeout(function() {
		this.paint();
	}, 50);

	function setupListeners(inputId, leftArrow, rightArrow, fn, initial) {
		var input = $("#" + inputId);
		input.numeric();
		input.val(initial);
		input.change(function() {
			adjust(input, fn, 0);
		});
		handleButton(leftArrow, input, fn, -1);
		handleButton(rightArrow, input, fn, 1);
	}

	function handleButton(buttonId, input, fn, increment) {
		var id = false;
		var timeout = false;
		var stop = function() {
			if (timeout) {
				window.clearTimeout(timeout);
				timeout = false;
				adjust(input, fn, increment);
			}
			if (id) {
				window.clearInterval(id);
				id = false;
			}
		};

		$("#" + buttonId).mousedown(function() {
			timeout = window.setTimeout(function() {
				id = window.setInterval(function() {
					timeout = false;
					adjust(input, fn, increment);
				}, 10);
			}, 250);
		}).mouseup(stop).focusout(stop).blur(stop).mouseleave(stop);
	}

	function adjust(input, callback, increment) {
		var n = parseInt(input.val());
		if (isNaN(n)) {
			n = 0;
		}
		var val = fix(n + increment);
		input.val(val);
		callback(val);
	}

	function fix(n) {
		while (n < -180) {
			n = n + 360;
		}
		while (n >= 180) {
			n = n - 360;
		}
		return n;
	}

	function updateAngleX(angle) {
		control.angleX = angle;
		control.paint();
	}

	function updateAngleY(angle) {
		control.angleY = angle;
		control.paint();
	}

	function updateAngleZ(angle) {
		control.angleZ = angle;
		control.paint();
	}
}

RotationControls.prototype.paint = function(ignoreListeners) {
	var width = 50, height = 100, depth = 10;
	var margin = 5, bottomMargin = 15;

	var canvas = document.getElementById("phoneposition");
	var ctx = canvas.getContext("2d");
	ctx.clearRect(0, 0, canvas.width, canvas.height);

	var r = 62;

	var xy = (this.angleX - 180) * Math.PI / 180;
	var yz = (this.angleY - 180) * Math.PI / 180;
	var xz = (180 - this.angleZ) * Math.PI / 180 + Math.PI / 2;

	var back = translateBack(xy, xz, yz);
	if ((back[0].z + back[2].z) / 2 < 0) {
		paint(canvas, ctx, back);
	} else {
		paint(canvas, ctx, translateFace(xy, xz, yz));
		paintScreen(canvas, ctx, translateScreen(xy, xz, yz));
	}

	var dz = 0;
	if (back[0].z > back[3].z) {
		var bottom = translateBottom(xy, xz, yz);
		paint(canvas, ctx, bottom);
		dz = bottom[1].y - bottom[0].y;
	} else if (back[0].z != back[3].z) {
		var top = translateTop(xy, xz, yz);
		paint(canvas, ctx, top);
		dz = top[1].y - top[0].y;
	}

	if (back[1].z > back[0].z) {
		paint(canvas, ctx, translateLeft(xy, xz, yz));
	} else if (back[1].z != back[0].z) {
		paint(canvas, ctx, translateRight(xy, xz, yz));
	}
	if (!ignoreListeners) {
		var accelX = (back[1].y - back[0].y) / width;
		var accelY = (back[0].y - back[3].y) / height;
		var accelZ = dz / depth;
		notifyAcceleration(accelX, accelY, accelZ);
	}
	function translateFace(xy, xz, yz) {
		var px = width / 2;
		var py = height / 2;
		var pz = depth / 2;
		var points = [ {
			x : px,
			y : py,
			z : pz
		}, {
			x : px,
			y : -py,
			z : pz
		}, {
			x : -px,
			y : -py,
			z : pz
		}, {
			x : -px,
			y : py,
			z : pz
		} ];
		return rotate(points, xy, xz, yz);
	}

	function translateScreen(xy, xz, yz) {
		var px = width / 2;
		var py = height / 2;
		var pz = depth / 2;
		var points = [ {
			x : px - margin,
			y : py - bottomMargin,
			z : pz
		}, {
			x : px - margin,
			y : -py + bottomMargin,
			z : pz
		}, {
			x : -px + margin,
			y : -py + margin,
			z : pz
		}, {
			x : -px + margin,
			y : py - margin,
			z : pz
		} ];
		return rotate(points, xy, xz, yz);
	}

	function translateBack(xy, xz, yz) {
		var px = width / 2;
		var py = height / 2;
		var pz = -depth / 2;
		var points = [ {
			x : px,
			y : py,
			z : pz
		}, {
			x : px,
			y : -py,
			z : pz
		}, {
			x : -px,
			y : -py,
			z : pz
		}, {
			x : -px,
			y : py,
			z : pz
		} ];
		return rotate(points, xy, xz, yz);
	}

	function translateTop(xy, xz, yz) {
		var px = width / 2;
		var py = height / 2;
		var pz = depth / 2;
		var points = [ {
			x : px,
			y : -py,
			z : pz
		}, {
			x : px,
			y : -py,
			z : -pz
		}, {
			x : px,
			y : py,
			z : -pz
		}, {
			x : px,
			y : py,
			z : pz
		} ];
		return rotate(points, xy, xz, yz);
	}

	function translateBottom(xy, xz, yz) {
		var px = -width / 2;
		var py = height / 2;
		var pz = depth / 2;
		var points = [ {
			x : px,
			y : -py,
			z : pz
		}, {
			x : px,
			y : -py,
			z : -pz
		}, {
			x : px,
			y : py,
			z : -pz
		}, {
			x : px,
			y : py,
			z : pz
		} ];
		return rotate(points, xy, xz, yz);
	}

	function translateLeft(xy, xz, yz) {
		var px = width / 2;
		var py = height / 2;
		var pz = depth / 2;
		var points = [ {
			x : px,
			y : py,
			z : pz
		}, {
			x : px,
			y : py,
			z : -pz
		}, {
			x : -px,
			y : py,
			z : -pz
		}, {
			x : -px,
			y : py,
			z : pz
		} ];
		return rotate(points, xy, xz, yz);
	}

	function translateRight(xy, xz, yz) {
		var px = width / 2;
		var py = -height / 2;
		var pz = depth / 2;
		var points = [ {
			x : px,
			y : py,
			z : pz
		}, {
			x : px,
			y : py,
			z : -pz
		}, {
			x : -px,
			y : py,
			z : -pz
		}, {
			x : -px,
			y : py,
			z : pz
		} ];
		return rotate(points, xy, xz, yz);
	}

	function rotate(points, xy, xz, yz) {
		var res = new Array();
		for ( var p in points) {
			var px = points[p].x;
			var py = points[p].y;
			var pz = points[p].z;

			var rx = Math.sqrt(px * px + py * py);
			var angXY = Math.atan(px / py) + (px < 0 ? Math.PI : 0);

			px = rx * Math.sin(angXY + xy);
			py = rx * Math.cos(angXY + xy);

			var rz = Math.sqrt(px * px + pz * pz);
			var angXZ = (px == 0 ? 0 : Math.atan(pz / px))
					+ (px < 0 ? Math.PI : 0);

			px = rz * Math.sin(angXZ + xz);
			pz = rz * Math.cos(angXZ + xz);

			var ry = Math.sqrt(py * py + pz * pz);
			var angYZ = (pz == 0 ? 0 : Math.atan(py / pz))
					+ (pz < 0 ? Math.PI : 0);

			py = ry * Math.sin(angYZ + yz);
			pz = ry * Math.cos(angYZ + yz);

			res.push( {
				x : px,
				y : py,
				z : pz
			});
		}
		return res;
	}

	function paint(canvas, ctx, points) {
		var xcoord = canvas.width / 2;
		var ycoord = canvas.height / 2;

		ctx.fillStyle = "yellow";
		ctx.strokeStyle = "black";
		ctx.beginPath();
		ctx.moveTo(xcoord + points[3].x, ycoord + points[3].y);
		for (point in points) {
			ctx.lineTo(xcoord + points[point].x, ycoord + points[point].y);
		}
		ctx.fill();
		ctx.beginPath();
		ctx.moveTo(xcoord + points[3].x, ycoord + points[3].y);
		for (point in points) {
			ctx.lineTo(xcoord + points[point].x, ycoord + points[point].y);
		}
		ctx.stroke();
	}

	function paintScreen(canvas, ctx, screen) {
		var xcoord = canvas.width / 2;
		var ycoord = canvas.height / 2;

		ctx.fillStyle = "grey";
		ctx.beginPath();
		ctx.moveTo(xcoord + screen[3].x, ycoord + screen[3].y);
		for (point in screen) {
			ctx.lineTo(xcoord + screen[point].x, ycoord + screen[point].y);
		}
		ctx.fill();
	}

	function notifyAcceleration(x, y, z) {
		accelerationCallback(x, y, z);
	}
};

RotationSupport.prototype.setAngles = function(x, y, z) {
	this.controls.angleX = x;
	this.controls.angleY = z; // It is extremly messy - this UI was developed
	// separately from the rest and follows
	this.controls.angleZ = y; // different conventions

	$("#xaxis").val(this.controls.angleX);
	$("#yaxis").val(this.controls.angleY);
	$("#zaxis").val(this.controls.angleZ);

	this.controls.paint(true);
};
