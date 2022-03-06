public interface Frame {
public void render();}
public class BasicFrame implements Frame{
@Override
public void render() {
System.out.println("If this were a real GUI, it'd display the frame");}}
public class FrameDecorator implements Frame {
Frame decoratedFrame;
public FrameDecorator(Frame wrappee) {
decoratedFrame = wrappee;}
public void render() {
decoratedFrame.render();}}
public class ScalingFrameDecorator extends FrameDecorator {
private double scaleFactor = 1;
public ScalingFrameDecorator(Frame wrappee, double scaleFactor) {
super(wrappee);
this.scaleFactor = scaleFactor;}
public void render() {
super.render();
System.out.println(String.format("Imagine making the frame %.1f times bigger!", scaleFactor));}
public void scale(double factor) {scaleFactor = factor;}}
public class ScrollingFrameDecorator extends FrameDecorator{
private int numScrollBars = 1;
public ScrollingFrameDecorator(Frame wrappee, int numScrollBars) {
super(wrappee);
this.numScrollBars = numScrollBars;}
public void render() {super.render();
System.out.println(String.format("Imagine adding %d scroll bars to the frame!", numScrollBars));}
public void addScrollBar() {numScrollBars++;}}
public class Main {
public static void main(String[] args) {
System.out.println("The basic frame");
Frame var1 = new BasicFrame();
var1.render();
System.out.println("");
FrameDecorator scrollFrame = new ScrollingFrameDecorator(var1, 2);
scrollFrame.render();
System.out.println("");
FrameDecorator scaleFrame = new ScalingFrameDecorator(scrollFrame, 7.0);
scaleFrame.render();}}
