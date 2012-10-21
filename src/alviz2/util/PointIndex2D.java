
package alviz2.util;

import java.util.Set;
import java.util.HashSet;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

import alviz2.graph.Node;

public class PointIndex2D {

	private class TreeNode {
		Node pt;
		TreeNode left, right;
	}

	private TreeNode root;
	private double minX, maxX, minY, maxY;

	public PointIndex2D() {
		root = null;
		minX = minY = Double.MAX_VALUE;
		maxX = maxY = Double.MIN_VALUE;
	}

	public void insert(Node p) {
		root = insert(root, p, true);
		minX = Math.min(minX, p.getPosition().getX());
		minY = Math.min(minY, p.getPosition().getY());
		maxX = Math.max(maxX, p.getPosition().getX());
		maxY = Math.max(maxY, p.getPosition().getY());
	}

	public boolean contains(Point2D q) {
		if(root == null)
			return false;

		return contains(root, q, true);
	}

	public Node nearest(Point2D q) {
		if(root == null)
			return null;

		return nearest(root, q, root.pt, new Rectangle2D(minX, minY, maxX-minX, maxY-minY), true);
	}

	public Set<Node> contains(Rectangle2D q) {
		Set<Node> rslt = new HashSet<Node>();
		if (root == null) {
			return rslt;
		}

		contains(root, q, rslt, new Rectangle2D(minX, minY, maxX-minX, maxY-minY), true);
		return rslt;
	}

	private void contains(TreeNode n, Rectangle2D q, Set<Node> rslt, Rectangle2D curRect, boolean even) {
		if(n == null)
			return;

		if(q.contains(n.pt.getPosition()))
			rslt.add(n.pt);

		Rectangle2D lRect, rRect;

		if(even) {
			lRect = new Rectangle2D(curRect.getMinX(), curRect.getMinY(), n.pt.getPosition().getX() - curRect.getMinX(), curRect.getHeight());
			rRect = new Rectangle2D(n.pt.getPosition().getX(), curRect.getMinY(), curRect.getMaxX() - n.pt.getPosition().getX(), curRect.getHeight());
		}
		else {
			lRect = new Rectangle2D(curRect.getMinX(), curRect.getMinY(), curRect.getWidth(), n.pt.getPosition().getY() - curRect.getMinY());
			rRect = new Rectangle2D(curRect.getMinX(), n.pt.getPosition().getY(), curRect.getWidth(), curRect.getMaxY() - n.pt.getPosition().getY());
		}

		if(q.intersects(lRect))
			contains(n.left, q, rslt, lRect, !even);
		if(q.intersects(rRect))
			contains(n.right, q, rslt, rRect, !even);

	}

	private Node nearest(TreeNode n, Point2D q, Node nearPt, Rectangle2D curRect, boolean even) {
		if (n == null) {
			return nearPt;
		}

		double distToNearPt = squareDistance(nearPt.getPosition(), q);
		if (squareDistance(n.pt.getPosition(), q) < distToNearPt) {
			nearPt = n.pt;
			distToNearPt = squareDistance(nearPt.getPosition(), q);
		}

		Rectangle2D lRect, rRect;

		if(even) {
			lRect = new Rectangle2D(curRect.getMinX(), curRect.getMinY(), n.pt.getPosition().getX() - curRect.getMinX(), curRect.getHeight());
			rRect = new Rectangle2D(n.pt.getPosition().getX(), curRect.getMinY(), curRect.getMaxX() - n.pt.getPosition().getX(), curRect.getHeight());
		}
		else {
			lRect = new Rectangle2D(curRect.getMinX(), curRect.getMinY(), curRect.getWidth(), n.pt.getPosition().getY() - curRect.getMinY());
			rRect = new Rectangle2D(curRect.getMinX(), n.pt.getPosition().getY(), curRect.getWidth(), curRect.getMaxY() - n.pt.getPosition().getY());
		}

		if (squareDistance(nearPt.getPosition(), lRect) < squareDistance(nearPt.getPosition(), rRect)) {
			if (squareDistance(q, lRect) <= distToNearPt) {
				nearPt = nearest(n.left, q, nearPt, lRect, !even);
				distToNearPt = squareDistance(nearPt.getPosition(), q);
			}
			if (squareDistance(q, rRect) <= distToNearPt) {
				nearPt = nearest(n.right, q, nearPt, rRect, !even);
			}
		}
		else {
			if (squareDistance(q, rRect) <= distToNearPt) {
				nearPt = nearest(n.right, q, nearPt, rRect, !even);
				distToNearPt = squareDistance(nearPt.getPosition(), q);
			}
			if (squareDistance(q, lRect) <= distToNearPt) {
				nearPt = nearest(n.left, q, nearPt, lRect, !even);
			}	
		}

		return nearPt;
	}

	private static double squareDistance(Point2D a, Point2D b) {
		double xd = a.getX() - b.getX();
		double yd = a.getY() - b.getY();
		return xd*xd + yd*yd;
	}

	private static double squareDistance(Point2D p, Rectangle2D r) {
		double xd = p.getX() < r.getMinX() ? (r.getMinX() - p.getX()) : (p.getX() > r.getMaxX() ? r.getMaxX() - p.getX() : 0.0);
		double yd = p.getY() < r.getMinY() ? (r.getMinY() - p.getY()) : (p.getY() > r.getMaxY() ? r.getMaxY() - p.getY() : 0.0);
		return xd*xd + yd*yd;
	}

	private boolean contains(TreeNode n, Point2D q, boolean even) {
		if(n == null)
			return false;

		if (n.pt.equals(q)) {
			return true;
		}

		boolean goLeft = even ? q.getX() <= n.pt.getPosition().getX() : q.getY() <= n.pt.getPosition().getY();
		return goLeft ? contains(n.left, q, !even) : contains(n.right, q, !even);
	}

	private TreeNode insert(TreeNode n, Node p, boolean even) {
		if(n == null) {
			n = new TreeNode();
			n.pt = p;
			n.left = null;
			n.right = null;
			return n;
		}

		boolean goLeft = even ? p.getPosition().getX() <= n.pt.getPosition().getX() : p.getPosition().getY() <= n.pt.getPosition().getY();
		if (goLeft)
			n.left = insert(n.left, p, !even);
		else
			n.right = insert(n.right, p, !even);
		return n;
	}	
}