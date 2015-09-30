package ssp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/* Fibonacci Node object */
class Node {
    int degree; /* degree of the node */
    Node child; /* child node object */
    int dist; /* data of the node */
    int vertex; /* index of the vertex, this node corresponds to */
    
    Node left_sibling; /* left sibling node */
    Node right_sibling; /* right sibling node */
    
    Node parent; /* parent node */
    Boolean childCut; /* childCut value, default = false */
    
    public Node(int data) {
        degree = 0;
        child = null;
        dist = data;
        vertex = -1;
        left_sibling = null;
        right_sibling = null;
        parent = null;
        childCut = false;
        
    }
    
    void addChild(Node node2) { /* add child node 'node2' to this node. Used in removeMin, where one node can be a child of another node */
        if(this.degree == 0) {
            this.child = node2;
            node2.right_sibling = node2;
            node2.left_sibling = node2;
        }
        else {
            Node child1 = this.child;
            node2.left_sibling = child1.left_sibling;
            node2.right_sibling = child1;
            child1.left_sibling.right_sibling = node2;
            child1.left_sibling = node2;
            
        }
        this.degree = this.degree + 1;
        node2.parent = this;
        node2.childCut = false; /* childCut is set to false */
        
    }
}

/* Heap Object */
class FHeap {
    
    private Node min; /* Root node in the heap which has least 'dist' value */
    
    FHeap() {
        min = null;
    }
    
    public void reduceKey(Node node, int value) { /* reduceKey operation of fibonacci heap. reduce node's dist value */
        if(node == null) {
            System.out.println("reduceKey called, but node is null");
            return;
        }
        //System.out.println("reduceKey called on " + node.vertex + " Value = " + value);
        if(node.parent == null) { /* If node is a root node, reduce the key directly, change min if required */
            node.dist = value;
            if(node != min && node.dist < min.dist)
                min = node;
        }
        else {
            if(value >= node.parent.dist) { /* If reduced value is greater than parent's value, directly reduce, nothing else to do */
                node.dist = value;
            }
            else {
                if(node.parent.degree == 1) { /* If parent has 1 child, set child to null since this child is to be cut from the tree */
                    node.parent.child = null;
                }
                else if(node.parent.degree > 1) {
                    /* Remove node from its sibling list */
                    node.left_sibling.right_sibling = node.right_sibling; 
                    node.right_sibling.left_sibling = node.left_sibling;
                    
                    /* Reset parent's child object if required */
                    if(node.parent.child == node)
                        node.parent.child = node.right_sibling;
                }
                else {
                    System.out.println("Inside reduceKey ... Parent's degree information error");
                    return;
                }
                node.parent.degree = node.parent.degree - 1; /* Parent's degree reduced by 1 */
                node.left_sibling = null;
                node.right_sibling = null;
                
                modifyChildCut(node.parent); /* Recursively check childCut values of parent nodes, and cut the parent nodes from the tree if required */
                node.parent = null;
                node.childCut = false;
                
                /* -1 is a special value used by modifyChildCut to forcefully cut nodes from tree whose childCut is already true */
                if(value != -1)
                    node.dist = value; /* We dont want to change values of nodes that are cut from the tree due to childChut = true */
                topLevelMerge(node); /* add Node as a root node to the heap */
            }
        }
    }
    
    /* func modifyChildCut: check childCut value. If false, and its not a root node, set it to true. If true, and its not a root node,
        cut the node from the tree and modifyChildCut of its parent using reduceKey function */
    private void modifyChildCut(Node node) {
        if(node.childCut == false && node.parent != null) 
            node.childCut = true;
        else if(node.childCut == true && node.parent != null) {
            reduceKey(node, -1); /* This will force the node to be cut from the tree and recursively call modifyChildCut on its parent, but the dist value of the node will not be changed */
        }
        
    }
    
    /* func removeMin: removeMin operation of Fibonacci Heap */
    public Node removeMin() {
        if(min == null) {
            System.out.println("removeMin called, but min is null.");
            return null;
        }
        Queue<Node> q = new LinkedList<Node>(); /* Queue to store all root nodes */
        Node min_node = min;
        Node child = min.child;
        
        /* Convert all child nodes of the min node to root nodes, and add to the queue */
        for(int i=0;i<min.degree;i++) {
            if(child == null) {
                System.out.println("Inside removeMin ... Degree > 0 but Child is NULL ... Exiting ... ");
                System.exit(1);
            }
            Node temp = child.right_sibling;
            child.childCut = false;
            child.parent = null;
            child.left_sibling = null;
            child.right_sibling = null;
            q.add(child);
            child = temp;
        }
        
        /* Add all the siblings of min node to the queue */
        Node temp = min.right_sibling;
        while(temp != min) {
            q.add(temp);
            temp = temp.right_sibling;
        }
        
        min = null;
        combineAndMerge(q); /* Pair wise combine all the root nodes in the queue */
        return min_node;
    }
    
    /* func combineAndMerge: Pairwise combine all root nodes in the queue */
    private void combineAndMerge(Queue q) {
        HashMap<Integer, Node> map = new HashMap<Integer, Node>();
        int maxDegree = 0; 
        while(!q.isEmpty()) {
            Node node1 = (Node)q.poll(); /* Get nodes from the queue */
            if(node1 == null){
                System.out.println("Error in combineAndMerge ... Node 1 is NULL");
            }
            else {
                while(map.containsKey(node1.degree)) { /* If hashmap already contains a root node of same degree, combine the 2 nodes */
                    Node node2 = map.get(node1.degree);
                    map.remove(node1.degree);
                    if(node1.dist <= node2.dist) {
                        node1.addChild(node2);
                    }
                    else {
                        node2.addChild(node1);
                        node1 = node2;
                    }
                }
                map.put(node1.degree, node1); /* Put the rootnode of the combined tree to the hashmap */
                if(node1.degree > maxDegree)
                    maxDegree = node1.degree;
            }
        }
        for(int i=0;i<=maxDegree;i++) {
            if(map.containsKey(i)) {
                topLevelMerge(map.get(i)); /* add root node to the heap */
                map.remove(i);
            }
        }
    }
    
    /* func addNode: add a node to the heap */
    public Node addNode(int dist) {
        Node node = new Node(dist);
        this.topLevelMerge(node); /* add this root node to the heap */
        
        return node;
    }
    
    /* func topLevelMerge: add root node to the heap and change min if required */
    private void topLevelMerge(Node node) {
        if(min == null) { /* No nodes in the heap */
            min = node;
            min.right_sibling = min;
            min.left_sibling = min;
        }
        else { /* Add node to the sibling list of min */
        node.left_sibling = min.left_sibling;
        node.right_sibling = min;
        min.left_sibling.right_sibling = node;
        min.left_sibling = node;
        if(node.dist < min.dist)
            min = node; /* Modify min */
        }
    }
}
