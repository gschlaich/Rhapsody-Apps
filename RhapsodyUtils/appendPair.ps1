$file = 'D:\schl591\Documents\GitHub\Rhapsody-Apps\RhapsodyUtils\src\main\java\de\schlaich\gunnar\rhapsody\utilities\RhapsodyReverseEngineering.java'
$pair = @"

class Pair<A, B>
{
    private final A first;
    private final B second;

    public Pair(A aFirst, B aSecond)
    {
        this.first  = aFirst;
        this.second = aSecond;
    }

    public A first()  { return first;  }
    public B second() { return second; }

    @Override
    public String toString()
    {
        return "(" + first + ", " + second + ")";
    }
}
"@
Add-Content -Path $file -Value $pair -Encoding UTF8
Write-Host "Pair class appended."
