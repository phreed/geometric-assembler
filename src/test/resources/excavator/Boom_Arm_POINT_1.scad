

translate(v = [0.0000000000, 0.0000000000, 0.0000000000]) {
	rotate(a = 0.0000000000, v = [0.0000000000, 0.0000000000, 0.0000000000]) {
		color( "Red", a=0.5 ) 
		polyhedron(
		points=[
		[3467.85, 43.0687, 302.5],
		[3455.57, 5.0, 332.5],
		[3455.57, 5.0, 302.5],
		[3455.57, 5.0, 300.0]],
		faces=[[0,1,2],[0,3,1],[0,2,3],[1,3,2]]);
		// import_stl(filename = "STL\\BOOM_EX_375.stl");
	}
}

translate(v = [12315.4098840541, -4259.9804627676, 903.0008418652]) {
	rotate(a = 179.9942740938, v = [-0.8894325067, 0.4570665301, -0.0000243586]) {
		color( "Blue", a=0.5 ) 
		polyhedron(
		points=[
		[-8649.51, 4688.51, 600.0],
		[-8625.71, 4720.65, 570.0],
		[-8625.71, 4720.65, 600.0],
		[-8625.71, 4720.65, 601.0]],
		faces=[[0,1,2],[0,3,1],[0,2,3],[1,3,2]]);
		// import_stl(filename = "STL\\BOOM.stl");
	}
}