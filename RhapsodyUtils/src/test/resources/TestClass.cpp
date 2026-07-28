// Test file for ASTHelper unit tests

namespace TestNamespace
{

class TestClass
{
public:
    TestClass();
    ~TestClass();
    
    void simpleMethod();
    int methodWithReturn();
    void methodWithParams(int a, const std::string& b);
    
private:
    int myMember;
};

TestClass::TestClass()
{
    //#[ operation TestClass()
    myMember = 0;
    //#]
}

TestClass::~TestClass()
{
    //#[ operation ~TestClass()
    // cleanup
    //#]
}

void TestClass::simpleMethod()
{
    //#[ operation simpleMethod()
    int x = 5;
    int y = 10;
    int z = x + y;
	
    //#]
}

int TestClass::methodWithReturn()
{
    //#[ operation methodWithReturn()
    return myMember;
    //#]
}

void TestClass::methodWithParams(int a, const std::string& b)
{
    //#[ operation methodWithParams(int,std::string)
    myMember = a;
    // use b somehow
    //#]
}

} // namespace TestNamespace
