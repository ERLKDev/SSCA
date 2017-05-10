// NOC: 0
class NOCTest1

// NOC: 1
class NOCTest2

// NOC: 2
class NOCTest3

// NOC: 1 (but nested class)
class NOCTest4

// NOC: 1 (but object)
class NOCTest5

// NOC: 2 (but objects)
class NOCTest6

// NOC: 1 (but trait)
class NOCTest7

// NOC: 2 (but traits)
class NOCTest8


// NOC: 0
trait NOCTest9

// NOC: 1
trait NOCTest10

// NOC: 2
trait NOCTest11

// NOC: 1 (but nested class)
trait NOCTest12

// NOC: 1 (but object)
trait NOCTest13

// NOC: 2 (but objects)
trait NOCTest14

// NOC: 1 (but trait)
trait NOCTest15

// NOC: 2 (but traits)
trait NOCTest16


class A1 extends NOCTest2

class B1 extends NOCTest3

class C1 extends NOCTest3

class D1 extends NOCTest4

class E1 extends D1

object F1 extends NOCTest5

object G1 extends NOCTest6

object H1 extends NOCTest6

trait I1 extends NOCTest7

trait J1 extends NOCTest8

trait K1 extends NOCTest8



class A2 extends NOCTest10

class B2 extends NOCTest11

class C2 extends NOCTest11

class D2 extends NOCTest12

class E2 extends D2

object F2 extends NOCTest13

object G2 extends NOCTest14

object H2 extends NOCTest14

trait I2 extends NOCTest15

trait J2 extends NOCTest16

trait K2 extends NOCTest16